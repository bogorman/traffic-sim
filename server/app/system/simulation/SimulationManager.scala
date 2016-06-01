package system.simulation

import akka.actor.{Actor, ActorRef, Props}
import shared.Constants
import shared.map.{CarsUpdate, Car => CarDAO}
import shared.geometry._
import shared.map.{Crossing, Road, RoadMap}
import shared.simulation.parameters.SimulationParameters
import system.simulation.SimulationManager.{CarRemoved, CarSpawned, CarsMoved, UpdateQueueCreated}
import system.simulation.strategy.FirstInFirstOutStrategy
import utils.MapUtils._

import scala.language.postfixOps
import scala.util.control.Exception.allCatch

object SimulationManager {

  case class UpdateQueueCreated(actorRef: ActorRef)

  case class CarsMoved(tick: Long, cars: Seq[Car]) extends StateChangedMessage

  case class CarRemoved(tick: Long, car: Car) extends StateChangedMessage

  case class CarSpawned(tick: Long, car: Car, target: Crossing) extends StateChangedMessage

}

class SimulationManager(map: RoadMap, socketAgent: ActorRef, simulationParameters: SimulationParameters) extends Actor {

  val crossingAgentsMap: Map[ActorRef, Crossing] = map.crossings map { c =>
    val actorRef = context actorOf(Props(classOf[CrossingAgent], c), s"crossing${c.name}")
    (actorRef, c)
  } toMap

  val roadsAgentsMap: Map[ActorRef, Road] = map.roads map { r =>
    val actorRef = context.actorOf(Props(classOf[RoadAgent], r), s"road${r.start.name}to${r.end.name}")
    (actorRef, r)
  } toMap

  val spawningAgent: ActorRef = context.actorOf(Props(classOf[SpawningAgent], map, simulationParameters.carsMaxNumber), "spawningAgent")

  override def receive: Receive = gatheringQueuesInfo(Map.empty, Map.empty, None)

  def gatheringQueuesInfo(roadQueues: Map[Road, ActorRef], crossingQueues: Map[Crossing, ActorRef], spawningAgentQueue: Option[ActorRef]): Receive = {
    case UpdateQueueCreated(queue) =>

      val (newRoadQueues, newCrossingQueues, newSpawningAgentQueue) = if (crossingAgentsMap contains sender) {
        (roadQueues, crossingQueues + (crossingAgentsMap(sender) -> queue), spawningAgentQueue)
      } else if (roadsAgentsMap contains sender) {
        (roadQueues + (roadsAgentsMap(sender) -> queue), crossingQueues, spawningAgentQueue)
      } else if (sender == spawningAgent) {
        (roadQueues, crossingQueues, Option(queue))
      } else {
        (roadQueues, crossingQueues, spawningAgentQueue)
      }

      if (newCrossingQueues.size == crossingAgentsMap.size && newRoadQueues.size == roadsAgentsMap.size && newSpawningAgentQueue.isDefined) {
        initialiseAll(newRoadQueues, newCrossingQueues, newSpawningAgentQueue.get)
        spawningAgent ! SpawningAgent.SpawningInit(crossingQueues filterKeys map.sources.toSet, crossingQueues filterKeys map.sinks.toSet, crossingQueues.values.toList)
        context become waitingForAck(crossingAgentsMap.keySet ++ roadsAgentsMap.keySet + spawningAgent)
      } else {
        context become gatheringQueuesInfo(newRoadQueues, newCrossingQueues, newSpawningAgentQueue)
      }
  }

  def initialiseAll(roadQueues: Map[Road, ActorRef], crossingQueues: Map[Crossing, ActorRef], spawningAgentQueue: ActorRef): Unit = {
    roadsAgentsMap foreach { case (actorRef, road) =>
      actorRef ! RoadAgent.RoadInit(crossingQueues(road.start), crossingQueues(road.end))
    }

    crossingAgentsMap foreach { case (actorRef, crossing) =>
      actorRef ! CrossingAgent.CrossingInit(roadQueues filterKeys crossing.reverseRoads.toSet,
        roadQueues filterKeys crossing.roads.toSet, spawningAgentQueue, FirstInFirstOutStrategy()) // todo proper injecting
    }
  }

  def waitingForAck(notConfirmedActors: Set[ActorRef]): Receive = {
    case Ack =>
      val newNotConfirmedActors = notConfirmedActors - sender
      if (newNotConfirmedActors.isEmpty) {
        startSimulation()
        context become gatheringSimulationData(Map.empty withDefaultValue List.empty,
          Map.empty withDefaultValue (crossingAgentsMap.size + roadsAgentsMap.size + 1), Map.empty, Seq.empty) // +1 for SpawningAgent
      } else {
        context become waitingForAck(newNotConfirmedActors)
      }
  }

  def startSimulation(): Unit =
    roadsAgentsMap.keys ++ crossingAgentsMap.keys ++ Iterable(spawningAgent) foreach {
      _ ! Start
    }

  def gatheringSimulationData(messages: Map[Long, List[StateChangedMessage]], ticks: Map[Long, Int],
                              cars: Map[String, CarDAO], speeds: Seq[Double]): Receive = {
    case msg: StateChangedMessage =>
      val current = msg.tick
      val (newTicks, newValue) = ticks.adjustWithValue(current) {
        _ - 1
      }
      val newMessages = messages.adjust(current) {
        msg :: _
      }
      if (newValue == 0) {
        val addedSpeeds: List[Double] = newMessages(current) flatMap {
          case CarsMoved(_, movedCars) => movedCars map {
            calculateDistance(cars) _
          }
          case _ => Seq.empty
        }

        val newCars: Map[String, CarDAO] = newMessages(current).foldLeft(cars) { case (acc, change) =>
          change match {
            case CarSpawned(_, car, _) => acc + (car.id -> car)
            case CarRemoved(_, car) => acc - car.id
            case CarsMoved(_, movedCars) => acc ++ movedCars.map(c => c.id -> (c: CarDAO)).toMap
            case NoOp(_) => acc
          }
        }

        val newSpeeds = addedSpeeds ++: speeds
        val avgSpeed: Double = allCatch opt {
          newSpeeds.sum / newCars.size
        } getOrElse 0

        if (current % Constants.statisticsInterval == 0) {
          context.parent ! CarsUpdate(newCars.values.toList, Option(avgSpeed))
          context become gatheringSimulationData(messages - current, ticks - current, newCars, Seq.empty)
        } else {
          context.parent ! CarsUpdate(newCars.values.toList, Option.empty)
          context become gatheringSimulationData(messages - current, ticks - current, newCars, newSpeeds)
        }
      } else {
        context become gatheringSimulationData(newMessages, newTicks, cars, speeds)
      }
  }

  private def calculateDistance(oldCars: Map[String, CarDAO])(newCar: Car): Double = distance(oldCars(newCar.id).location, newCar.x >< newCar.y)
}
