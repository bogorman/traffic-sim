package shared

import scala.math.pow

package object map {

  trait MapApi {
    def test(): String
  }

  class RoadMap(crossingDefs: List[CrossingDef], roadDefs: List[RoadDef]) {

    private[map] val roadMap: Map[String, List[Road]] = roadDefs groupBy { r => r.start } mapValues { _ map { new Road(this, _) } }

    val crossingsMap: Map[String, Crossing] = crossingDefs map { c => c.name -> new Crossing(this, c) } toMap

    def crossings: List[Crossing] = crossingsMap.values.toList

    def roads: List[Road] = roadMap.values.flatten.toList.distinct
  }

  class Road(map: RoadMap, definition: RoadDef) {
    def start: Crossing = map.crossingsMap(definition.start)

    def end: Crossing = map.crossingsMap(definition.end)

    def length: Double = (start.coordinates :: bendingPoints ::: end.coordinates :: Nil) sliding 2 map { cs => distance(cs.head, cs.last)} sum

    def bendingPoints: List[Coordinates] = definition.bendingPoints
  }

  class Crossing(private val map: RoadMap, private val definition: CrossingDef) {

    def roads: List[Road] = map.roadMap(definition.name)

    def name: String = definition.name

    def coordinates: Coordinates = definition.coordinates

    override def equals(obj: scala.Any): Boolean = obj match {
      case that: Crossing => (this.map eq that.map) && this.definition == that.definition
      case _              => false
    }

    override def hashCode(): Int = map.## * 37 + definition.## * 7
  }

  case class CrossingDef(name: String, coordinates: Coordinates)

  case class RoadDef(start: String, end: String, bendingPoints: List[Coordinates])

  case class Coordinates(x: Double, y: Double) {
    override def toString: String = s"$x >< $y"
  }

  implicit class Double2coordinates(x: Double) {
    def ><(y: Double): Coordinates = Coordinates(x, y)
  }

  private def distance(a: Coordinates, b: Coordinates) = pow(pow(a.x - b.x, 2) + pow(a.y - b.y, 2), 0.5)
}
