package system.simulation

import akka.actor.ActorRef
import shared.map.Road

case class Car(id: String, x: Double, y: Double, supervisor: ActorRef, route: List[Road])
