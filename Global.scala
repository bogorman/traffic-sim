import play.Logger
import play.api.{Application, GlobalSettings}
import akka.actor.{ActorSystem, Props}
import system._

object Global extends GlobalSettings {
  var system: ActorSystem = null

  override def onStart(app: Application) {
    system = ActorSystem()

    // val simulationProxyActor = system.actorOf(Props(new SimulationProxy), "SimulationProxy")

    println("STARTING SIM SERVER")
  }

  override def onStop(app: Application) {
    println("STOPPING SIM SERVER")
  }

}

