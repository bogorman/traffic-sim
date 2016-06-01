package system

import java.io.FileInputStream

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import play.api.libs.json.Json
import shared.map.{CarsUpdate, RoadMap}
import shared.simulation.parameters.SimulationParameters
import system.simulation.SimulationManager
import upickle.default._
import play.api.libs.json.Json
import utils.map.json.MapReads

import scala.language.postfixOps

class SocketAgent(out: ActorRef) extends Actor {

  override def receive: Receive = waitingForSimulationParameter

  def waitingForSimulationParameter: Receive = {
    case simParams: String =>
      val simulationParameters = read[SimulationParameters](simParams)
      val map = Json.parse(new FileInputStream("map.json")).as[RoadMap]
      out ! write(map)
      context.actorOf(Props(classOf[SimulationManager], map, self, simulationParameters), "simulationManager")

    case carsList: CarsUpdate =>
      out ! write(carsList)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    println("!!! stopped !!!")
    context.children foreach {
      _ ! PoisonPill
    }
  }
}
