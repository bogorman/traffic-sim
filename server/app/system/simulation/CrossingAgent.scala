package system.simulation

import akka.actor.ActorRef
import shared.Constants
import shared.geometry.segmentOffset
import shared.map.{Crossing, Road}
import system.simulation.CrossingAgent.{CrossingInit, CrossingState}
import system.simulation.RoadAgent.{CarTaken, LeaveCrossing}
import system.simulation.SimulationManager.{CarRemoved, CarSpawned, CarsMoved}
import system.simulation.SpawningAgent.CrossingFreed
import system.simulation.strategy.CrossingStrategy


object CrossingAgent {

  // supported changes
  case class EnterCrossing(tick: Long, car: Car) extends TickMsg

  case class UnblockRoad(tick: Long, road: Road) extends TickMsg

  case class SpawnCar(tick: Long, car: Car) extends TickMsg

  // init message
  case class CrossingInit(inRoads: Map[Road, ActorRef], outRoads: Map[Road, ActorRef], spawningAgent: ActorRef, crossingStrategy: CrossingStrategy) extends SimulationAgent.AgentInit {
    override def neighbours: List[ActorRef] = inRoads.values.toList ++ outRoads.values ++ Iterable(spawningAgent)
  }

  // state
  case class CrossingState(crossing: Crossing, currentCar: Option[Car], inRoads: Map[Road, ActorRef], outRoads: Map[Road, ActorRef],
                           blockedRoads: Set[Road], simulationManager: ActorRef, spawningAgent: ActorRef, carToSpawn: Option[Car], crossingStrategy: CrossingStrategy
                          )
    extends SimulationAgent.AgentState[CrossingState] {

    override def update(changes: List[TickMsg]): CrossingState = changes.foldLeft(this) {
      case (self, EnterCrossing(t, car)) =>
        self.copy(crossingStrategy = self.crossingStrategy.addCar(car))
      case (self, UnblockRoad(_, road)) => self.copy(blockedRoads = self.blockedRoads - road)
      case (self, SpawnCar(_, car)) => self.copy(carToSpawn = Option(car))
    }

    override def nextStep: (CrossingState, Map[ActorRef, (Long) => TickMsg]) = {
      currentCar match {
        // zjezdzanie ze skrzyzowania
        case Some(car@Car(_, _, _, _, nextRoad :: rest)) =>
          val coordinates = segmentOffset(nextRoad.start.coordinates, nextRoad.end.coordinates, Constants.crossingDiameter)
          val newRoad: ActorRef = outRoads(nextRoad)
          val newCar = car.copy(x = coordinates.x, y = coordinates.y, supervisor = newRoad, route = rest)
          (copy(currentCar = None, blockedRoads = blockedRoads + nextRoad), msgMap +(
            simulationManager -> { CarsMoved(_, Seq(newCar)) },
            newRoad -> { LeaveCrossing(_, newCar) },
            spawningAgent -> { CrossingFreed(_, crossing) }))

        case Some(car) => // znikanie auta, ktore dojechalo do celu
          (copy(currentCar = None), msgMap +(
            simulationManager -> {
              CarRemoved(_, car)
            },
            spawningAgent -> {
              CrossingFreed(_, crossing)
            }))

        case _ => // kiedy skrzyzowanie jest puste
          if (carToSpawn.isDefined && !blockedRoads.contains(carToSpawn.get.route.head)) {
            // tworzenie nowego auta
            (copy(currentCar = carToSpawn, carToSpawn = None), msgMap + (
              simulationManager -> {
                CarSpawned(_, carToSpawn.get, carToSpawn.get.route.last.end)
              }
              ))
          } else {
            crossingStrategy.nextCar(blockedRoads) match {
              case (Some(car), crossingStrategy: CrossingStrategy) =>
                val newCar = car.copy(x = crossing.coordinates.x, y = crossing.coordinates.y)
                (copy(currentCar = Some(newCar), crossingStrategy = crossingStrategy), msgMap +(
                  simulationManager -> {
                    CarsMoved(_, Seq(newCar))
                  },
                  car.supervisor -> {
                    CarTaken(_)
                  }))
              case (None, crossingStrategy: CrossingStrategy) =>
                (copy(crossingStrategy = crossingStrategy), msgMap)
            }
          }

//                  if (waitingCars.nonEmpty) { // tutaj strategia
//                    waitingCars.dequeue match {
//                      case (car@Car(_, _, _, _, nextRoad :: _), newQueue) =>
//                        if (blockedRoads contains nextRoad) { // zablokowana droga
//                          (copy(waitingCars = newQueue enqueue car), msgMap)
//                        } else { // wjazd na skrzyzowanie
//                          val newCar = car.copy(x = crossing.coordinates.x, y = crossing.coordinates.y)
//                          (copy(currentCar = Some(newCar), waitingCars = newQueue), msgMap +(
//                            car.supervisor -> {CarTaken(_)},
//                            simulationManager -> {CarsMoved(_, Seq(newCar))}))
//                        }
//
//                      case (car, newQueue) => // wjezdza na ostatnie skrzyzowanie
//                        val newCar = car.copy(x = crossing.coordinates.x, y = crossing.coordinates.y)
//                        (copy(currentCar = Some(newCar), waitingCars = newQueue), msgMap +(
//                          simulationManager -> {CarsMoved(_, Seq(newCar))},
//                          car.supervisor -> {CarTaken(_)}))
//                    }
//                  } else {
//                    (this, msgMap) // to pewnie niepotrzebne
//                  }
      }
    }
  }

}

class CrossingAgent(crossing: Crossing) extends SimulationAgent[CrossingState, CrossingInit](crossing.roads.length + crossing.reverseRoads.length + 1) {

  override def clearState(init: CrossingInit): CrossingState = CrossingState(
    crossing = crossing,
    currentCar = None,
    inRoads = init.inRoads,
    outRoads = init.outRoads,
    blockedRoads = Set.empty,
    simulationManager = context.parent,
    spawningAgent = init.spawningAgent,
    carToSpawn = None,
    crossingStrategy = init.crossingStrategy)
}