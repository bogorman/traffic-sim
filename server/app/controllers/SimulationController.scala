package controllers

import akka.actor.Props
import com.google.inject.Inject
import play.api.Play.current
import play.api.mvc.WebSocket
import system.{ActorManager, SocketAgent}

class SimulationController @Inject() (actorManager: ActorManager) {

  def startSimulation() = WebSocket.acceptWithActor[String, String] { request =>
    ref => Props(new SocketAgent(ref, actorManager))
  }
}
