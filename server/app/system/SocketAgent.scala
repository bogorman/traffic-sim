package system

import java.io.FileInputStream

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import play.api.libs.json.Json
import shared.map.{CarsUpdate, RoadMap}
import shared.simulation.parameters.{CrossingStrategyEnum, CustomEnumerationSerialization, MapFileEnum, SimulationParameters}
import system.simulation.SimulationManager
import upickle.default._
import play.api.libs.json.Json
import utils.map.json.MapReads

import scala.language.postfixOps

class SocketAgent(out: ActorRef) extends Actor with CustomEnumerationSerialization {

  override def receive: Receive = waitingForSimulationParameter

  var previousSimulationRef = Option.empty[ActorRef]

  var simulationCounter = 0

  def waitingForSimulationParameter: Receive = {
    case simParams: String =>
      val simulationParameters = read[SimulationParameters](simParams)
      val mapFile = new FileInputStream(MapFileEnum.toResourceFile(simulationParameters.mapFile))
      val map = Json.parse(mapFile).as[RoadMap]
      out ! write(map)
      previousSimulationRef.foreach( _ ! PoisonPill)
      previousSimulationRef = Option(
        context.actorOf(Props(classOf[SimulationManager], map, self, simulationParameters), s"simulationManager$simulationCounter")
      )
      simulationCounter += 1

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
