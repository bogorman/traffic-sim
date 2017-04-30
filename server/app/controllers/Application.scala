package controllers

import com.google.inject.Singleton
import play.api.mvc._
import akka.actor.Props

import play.api.libs.concurrent.Akka
import play.api.Play.current
import system._

@Singleton
class Application extends Controller {
	
	val simulationProxyActor = Akka.system.actorOf(Props(new SimulationProxy), "SimulationProxy")


  def index = Action {
    Ok(views.html.index())
  }
}
