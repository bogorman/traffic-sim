package system

import akka.actor.{ActorRef, ActorSystem, Props}

object ActorManager {

  private lazy val system: ActorSystem = ActorSystem("traffic-sim")

  lazy val mapAgent: ActorRef = system.actorOf(Props[MapAgent], "map-agent")

}
