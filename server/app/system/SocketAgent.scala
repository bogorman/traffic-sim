package system

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.JsArray
import shared.car.{Car, CarsList}
import shared.map.RoadMap
import system.MapAgent.GetMap
import system.simulation.SimulationManager
import shared.geometry._
import scala.language.postfixOps
import upickle.default._

class SocketAgent(out: ActorRef, manager: ActorManager) extends Actor {

  manager.mapAgent ! GetMap

  override def receive: Receive = waitingForMap

  def waitingForMap: Receive = {
    case map: RoadMap =>
//      out ! map.toString
      context.system actorOf Props(classOf[SimulationManager], map, self)
      context become forwardingSimulationData
  }

  def forwardingSimulationData: Receive = {
    case msg: String if msg == "carTest" => {
//      out ! write(Seq(1, 2, 3))
      out ! write(CarsList(List(Car(50.0><50.0, "#AAAAAA"))))
//    out ! "awesomeAnswer"
    }
    case msg: String =>
      println (msg)
    case msg: JsArray =>
//      out ! msg
  }
}
