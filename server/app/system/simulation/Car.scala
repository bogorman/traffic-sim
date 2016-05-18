package system.simulation

import akka.actor.ActorRef
import shared.map.Road
import shared.geometry._

import scala.language.implicitConversions

case class Car(id: String, x: Double, y: Double, supervisor: ActorRef, route: List[Road]) {
  def color: String = "#551A8B"
}

object Car {
  import shared.car.{Car => CarDAO}
  implicit def car2car(c: Car): CarDAO = CarDAO(c.x >< c.y, c.color)
}
