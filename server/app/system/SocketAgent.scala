package system

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.JsArray
import shared.map.RoadMap
import system.MapAgent.GetMap
import system.simulation.{SimulationManager}

import scala.language.postfixOps

class SocketAgent(out: ActorRef, manager: ActorManager) extends Actor {

  manager.mapAgent ! GetMap

  override def receive: Receive = waitingForMap

  def waitingForMap: Receive = {
    case map: RoadMap =>
      out ! map.toString
      context.system actorOf Props(classOf[SimulationManager], map, self)
      context become forwardingSimulationData
  }

  def forwardingSimulationData: Receive = {
    case msg: JsArray =>
      out ! msg
  }
}
