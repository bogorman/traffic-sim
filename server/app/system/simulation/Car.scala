package system.simulation

import akka.actor.ActorRef
import shared.map.Road
import shared.geometry._

import scala.language.implicitConversions

case class Car(id: String, x: Double, y: Double, previousLocation: Option[Coordinates], supervisor: ActorRef, route: List[Road]) {
  def color: String = {
    s"#${Integer.toHexString(id.##)}00000" take 7
  }
  def copyWithNewCoordinates(coordinates: Coordinates): Car = {
    copy(x = coordinates.x, y = coordinates.y, previousLocation = Option(x >< y))
  }
}

object Car {
  import shared.map.{Car => CarDAO}
  implicit def car2car(c: Car): CarDAO = CarDAO(c.id, c.x >< c.y, c.color, c.previousLocation)
}