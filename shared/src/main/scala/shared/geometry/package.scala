package shared


import scala.math._

package object geometry {
  case class Coordinates(x: Double, y: Double) {
    override def toString: String = s"$x >< $y"
  }

  implicit class Double2coordinates(x: Double) {
    def ><(y: Double): Coordinates = Coordinates(x, y)
  }

  def distance(a: Coordinates, b: Coordinates) = pow(pow(a.x - b.x, 2) + pow(a.y - b.y, 2), 0.5)

  def segmentOffset(a: Coordinates, b: Coordinates, dist: Double): Coordinates = {
    val sin = (b.y - a.y) / distance(a, b)
    val cos = (b.x - a.x) / distance(a, b)
    (a.x + cos * dist) >< (a.y + sin * dist)
  }
}
