package controllers

import akka.actor.Props
import play.api.Play.current
import play.api.mvc.WebSocket
// import system.SocketAgent

import jsactor.bridge.server.UPickleServerBridgeActor

import jsactor.bridge.protocol.UPickleBridgeProtocol
import shared.SimulationProtocol

import play.api.libs.concurrent.Akka
// import play.api.mvc._

import system._

class SimulationController {

  // def getSimulationActor() {
  // 	val a = Akka.system.actorFor("/user/SimulationProxy")
  // 	if (a.isTerminated){
  // 		Akka.system.actorOf(Props(new SimulationProxy), "SimulationProxy")
  // 	} else {
  // 		a
  // 	}
  // }
  // val simulationProxyActor = getSimulationActor()

  def startSimulation() = WebSocket.acceptWithActor[String, String] { req ⇒ websocket ⇒
    implicit val bridgeProtocol = SimulationProtocol
    UPickleServerBridgeActor.props(websocket)
  }

  // def startSimulation() = WebSocket.acceptWithActor[String, String] { request =>
  //   ref => Props(new SocketAgent(ref))
  // }
}
