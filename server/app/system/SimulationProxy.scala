package system

import java.io.FileInputStream

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import play.api.libs.json.Json
import shared.map.{CarsUpdate, RoadMap}

import shared.simulation.parameters._

import system.simulation.SimulationManager
import upickle.default._
import play.api.libs.json.Json
import utils.map.json.MapReads

import scala.language.postfixOps

class SimulationProxy extends Actor {

  var previousSimulationRef = Option.empty[ActorRef]

  var simulationCounter = 0

  var subscribers = Set.empty[ActorRef]

  override def receive: Receive = {    
  
    case Terminated(actor) => {
      println("Terminated. Removing Subscriber")
      subscribers -= actor    
    }

    case simParams: SimulationParameters => {    
      if (!subscribers.contains(sender())){
        println("SimulationParameters arrived. Adding Subscriber")
        context watch sender()
        subscribers += sender()
      } else {
        println("SimulationParameters arrived. Ignoring Subscriber")
      }

      val mapFile = new FileInputStream(MapFileEnum.toResourceFile(simParams.mapFile))
      println("Loaded mapFile")
      val map = Json.parse(mapFile).as[RoadMap]
      println("Sending map file.")
      sender ! map
      println("Sent map file.")
      previousSimulationRef.foreach( _ ! PoisonPill)
      previousSimulationRef = Option(
        context.actorOf(Props(classOf[SimulationManager], map, self, simParams), s"simulationManager$simulationCounter")
      )
      simulationCounter += 1
    }

    case carsList: CarsUpdate => {
      // out ! write(carsList)
      if (subscribers.size > 0){
        subscribers.head ! carsList
      }
    }
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    println("!!! stopped !!!")
    context.children foreach {
      _ ! PoisonPill
    }
  }
}
