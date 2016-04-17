package system

import akka.actor.{Actor, ActorRef, Props}
import shared.map.RoadMap
import system.MapAgent.GetMap
import system.simulation.{SimulationManager, SimulationManager$}

import scala.language.postfixOps

class SocketAgent(out: ActorRef, manager: ActorManager) extends Actor {

  manager.mapAgent ! GetMap

  override def receive: Receive = waitForMap

  def waitForMap: Receive = {
    case map: RoadMap =>
      out ! map.toString
      context.system actorOf Props(classOf[SimulationManager], map, self)
  }
}
