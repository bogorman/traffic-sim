package controllers

import akka.actor.Props
import play.api.Play.current
import play.api.mvc.WebSocket
import system.SocketAgent

class SimulationController {

  def startSimulation() = WebSocket.acceptWithActor[String, String] { request =>
    ref => Props(new SocketAgent(ref))
  }
}
