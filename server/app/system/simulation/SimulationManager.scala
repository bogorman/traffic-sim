package system.simulation

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString}
import shared.map.{Crossing, Road, RoadMap}
import system.simulation.SimulationManager.UpdateQueueCreated

import scala.language.postfixOps

import utils.MapUtils._

object SimulationManager {

  case class UpdateQueueCreated(actorRef: ActorRef)

  case class CarsMoved(tick: Long, cars: Seq[Car]) extends EndMessage {
    override def json: JsArray = JsArray(cars map {
      case Car(id, x, y, _, _) => JsObject(Seq(
        "moved" -> JsString(id), "x" -> JsNumber(x), "y" -> JsNumber(y)))
    })
  }

  case class CarRemoved(tick: Long, car: Car) extends EndMessage {
    override def json: JsArray = JsArray(Seq(JsObject(Seq("removed" -> JsString(car.id)))))
  }

  case class CarSpawned(tick: Long, car: Car, target: Crossing) extends EndMessage {
    override def json: JsArray = JsArray(Seq(JsObject(Seq(
      "spawned" -> JsString(car.id), "x" -> JsNumber(car.x), "y" -> JsNumber(car.y), "to" -> JsString(target.name)))))
  }

}

class SimulationManager(map: RoadMap, outputStream: ActorRef) extends Actor {

  val crossingAgentsMap: Map[ActorRef, Crossing] = map.crossings map { c =>
    val actorRef = context actorOf Props(classOf[CrossingAgent], c)
    (actorRef, c)
  } toMap

  val roadsAgentsMap: Map[ActorRef, Road] = map.roads map { r =>
    val actorRef = context actorOf Props(classOf[RoadAgent], r)
    (actorRef, r)
  } toMap

  override def receive: Receive = gatheringQueuesInfo(Map(), Map())

  def gatheringQueuesInfo(roadQueues: Map[Road, ActorRef], crossingQueues: Map[Crossing, ActorRef]): Receive = {
    case UpdateQueueCreated(queue) =>

      val (newRoadQueues, newCrossingQueues) = if (crossingAgentsMap contains sender) {
        (roadQueues, crossingQueues + (crossingAgentsMap(sender) -> queue))
      } else if (roadsAgentsMap contains sender) {
        (roadQueues + (roadsAgentsMap(sender) -> queue), crossingQueues)
      } else {
        (roadQueues, crossingQueues)
      }
      if (newCrossingQueues.size == crossingAgentsMap.size && newRoadQueues.size == roadsAgentsMap.size) {
        initialiseAll(newRoadQueues, newCrossingQueues)
        context become waitingForAck(crossingAgentsMap.keySet ++ roadsAgentsMap.keySet)
      } else {
        context become gatheringQueuesInfo(newRoadQueues, newCrossingQueues)
      }
  }

  def initialiseAll(roadQueues: Map[Road, ActorRef], crossingQueues: Map[Crossing, ActorRef]): Unit = {
    roadsAgentsMap foreach { case (actorRef, road) =>
      actorRef ! RoadAgent.RoadInit(crossingQueues(road.start), crossingQueues(road.end))
    }

    crossingAgentsMap foreach { case (actorRef, crossing) =>
      actorRef ! CrossingAgent.CrossingInit(roadQueues filterKeys crossing.roads.toSet, roadQueues filterKeys crossing.reverseRoads.toSet)
    }
  }

  def waitingForAck(notConfirmedActors: Set[ActorRef]): Receive = {
    case Ack =>
      val newNotConfirmedActors = notConfirmedActors - sender
      if (newNotConfirmedActors.isEmpty) {
        startSimulation()
        println(s"crossings: ${crossingAgentsMap.size}, roads: ${roadsAgentsMap.size}")
        context become gatheringSimulationData(Map() withDefaultValue JsArray(), Map() withDefaultValue (crossingAgentsMap.size + roadsAgentsMap.size))
      } else {
        context become waitingForAck(newNotConfirmedActors)
      }
  }

  def startSimulation(): Unit = roadsAgentsMap.keys ++ crossingAgentsMap.keys foreach {
    _ ! Start
  }

  def gatheringSimulationData(messages: Map[Long, JsArray], ticks: Map[Long, Int]): Receive = {
    case msg: EndMessage =>
      val current = msg.tick
      val (newTicks, newValue) = ticks.adjustWithValue(current) {
        _ - 1
      }
      val newMessages = messages.adjust(current) {
        _ ++ msg.json
      }
      println(newValue)
      if (newValue == 0) {
        context.parent ! messages(current)
        context become gatheringSimulationData(messages - current, ticks - current)
      } else {
        context become gatheringSimulationData(newMessages, newTicks)
      }
  }
}
