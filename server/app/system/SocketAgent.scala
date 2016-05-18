package system

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import play.api.libs.json.JsArray
import shared.car.{Car, CarsList}
import shared.map.RoadMap
import system.MapAgent.GetMap
import system.simulation.SimulationManager
import shared.geometry._

import scala.language.postfixOps
import upickle.default._

class SocketAgent(out: ActorRef, manager: ActorManager) extends Actor {

  val serializedMockCarsList = write(CarsList(List(Car(50.0><50.0, "#FFC0CB"))))

  manager.mapAgent ! GetMap

  override def receive: Receive = waitingForMap

  def waitingForMap: Receive = {
    case map: RoadMap =>
      context actorOf Props(classOf[SimulationManager], map, self)
      context become forwardingSimulationData
  }

  def forwardingSimulationData: Receive = {
    case carsList: CarsList =>
      out ! write(carsList)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit =  {
    context.children foreach { _ ! PoisonPill }
  }
}
