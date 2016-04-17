package system

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.{Inject, Singleton}

@Singleton
class ActorManager @Inject() (system: ActorSystem) {

  lazy val mapAgent: ActorRef = system.actorOf(Props[MapAgent], "map-agent")

}
