package system.simulation

import akka.actor.ActorRef
import shared.Constants
import shared.geometry.segmentOffset
import shared.map.{Crossing, Road}
import system.simulation.CrossingAgent.{CrossingInit, CrossingState}
import system.simulation.RoadAgent.{CarTaken, LeaveCrossing}
import system.simulation.SimulationManager.{CarRemoved, CarSpawned, CarsMoved}
import system.simulation.SpawningAgent.CrossingFreed

import scala.collection.immutable.Queue


object CrossingAgent {

  // supported changes
  case class EnterCrossing(tick: Long, car: Car) extends TickMsg

  case class UnblockRoad(tick: Long, road: Road) extends TickMsg

  case class SpawnCar(tick: Long, car: Car) extends TickMsg

  // init message
  case class CrossingInit(inRoads: Map[Road, ActorRef], outRoads: Map[Road, ActorRef], spawningAgent: ActorRef) extends SimulationAgent.AgentInit {
    override def neighbours: List[ActorRef] = inRoads.values.toList ++ outRoads.values ++ Iterable(spawningAgent)
  }

  // state
  case class CrossingState(crossing: Crossing, currentCar: Option[Car], waitingCars: Queue[Car], inRoads: Map[Road, ActorRef], outRoads: Map[Road, ActorRef],
                           blockedRoads: Set[Road], controller: ActorRef, spawningAgent: ActorRef, carToSpawn: Option[Car])
    extends SimulationAgent.AgentState[CrossingState] {

    override def update(changes: List[TickMsg]): CrossingState = changes.foldLeft(this) {
      case (self, EnterCrossing(_, car)) => self.copy(waitingCars = waitingCars.enqueue(car))
      case (self, UnblockRoad(_, road)) => self.copy(blockedRoads = blockedRoads - road)
      case (self, SpawnCar(_, car)) => self.copy(carToSpawn = Option(car))
    }

    override def nextStep: (CrossingState, Map[ActorRef, (Long) => TickMsg]) = currentCar match {
      case Some(car@Car(_, _, _, _, nextRoad :: rest)) =>
        val coordinates = segmentOffset(nextRoad.start.coordinates, nextRoad.end.coordinates, Constants.crossingDiameter)
        val newRoad: ActorRef = outRoads(nextRoad)
        val newCar = car.copy(x = coordinates.x, y = coordinates.y, supervisor = newRoad, route = rest)
        (copy(currentCar = None, blockedRoads = blockedRoads + nextRoad), msgMap + (
          controller -> {CarsMoved(_, Seq(newCar))},
          newRoad -> {LeaveCrossing(_, newCar)},
          spawningAgent -> {CrossingFreed(_, crossing)}))

      case Some(car) =>
        (copy(currentCar = None), msgMap + (
          controller -> {CarRemoved(_, car)},
          spawningAgent -> {CrossingFreed(_, crossing)}))

      case _ =>
        if (carToSpawn.isDefined) {
          (copy(currentCar = carToSpawn, carToSpawn = None), msgMap + (
              controller -> { CarSpawned(_, carToSpawn.get, carToSpawn.get.route.last.end)}
            ))
        } else if (waitingCars.nonEmpty) {
          waitingCars.dequeue match {
            case (car@Car(_, _, _, _, nextRoad :: _), newQueue) =>
              if (blockedRoads contains nextRoad) {
                (copy(waitingCars = newQueue enqueue car), msgMap)
              } else {
                val newCar = car.copy(x = crossing.coordinates.x, y = crossing.coordinates.y)
                (copy(currentCar = Some(newCar), waitingCars = newQueue), msgMap +(
                  car.supervisor -> { CarTaken(_) },
                  controller -> { CarsMoved(_, Seq(newCar)) }))
              }

            case (car, newQueue) =>
              val newCar = car.copy(x = crossing.coordinates.x, y = crossing.coordinates.y)
              (copy(currentCar = Some(newCar), waitingCars = newQueue), msgMap + (
                controller -> { CarsMoved(_, Seq(newCar)) },
                car.supervisor -> { CarTaken(_) }))
          }
        } else {
          (this, msgMap)
        }
    }
  }
}

class CrossingAgent(crossing: Crossing) extends SimulationAgent[CrossingState, CrossingInit](crossing.roads.length + crossing.reverseRoads.length + 1) {

  override def clearState(init: CrossingInit): CrossingState = CrossingState(crossing, None, Queue(), init.inRoads, init.outRoads, Set(), context.parent,
   init.spawningAgent, None)
}