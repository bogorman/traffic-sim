package shared

import shared.geometry._

import scala.language.postfixOps

package object map {

  trait MapApi {
    def test(): String
  }

  class RoadMap(crossingDefs: List[CrossingDef], roadDefs: List[RoadDef]) {

    val crossingsMap: Map[String, Crossing] = crossingDefs map { c => c.name -> new Crossing(this, c) } toMap
    private[map] val roadMap: Map[String, List[Road]] = roadDefs groupBy {
      _.start
    } mapValues {
      _ map {
        new Road(this, _)
      }
    }
    private[map] val reverseRoadMap: Map[String, List[Road]] = roadMap.values.flatten.toList groupBy {
      _.end.name
    }

    def crossings: List[Crossing] = crossingsMap.values.toList

    def roads: List[Road] = roadMap.values.flatten.toList.distinct

    override def toString: String = s"{${crossings ++ roads}}"
  }

  class Road(map: RoadMap, definition: RoadDef) {

    def length: Double = (start.coordinates :: bendingPoints ::: end.coordinates :: Nil) sliding 2 map { cs => distance(cs.head, cs.last) } sum

    def start: Crossing = map.crossingsMap(definition.start)

    def end: Crossing = map.crossingsMap(definition.end)

    def bendingPoints: List[Coordinates] = definition.bendingPoints

    override def equals(obj: scala.Any): Boolean = obj match {
      case that: Road => (this.map eq that.map) && this.definition == that.definition
    }

    override def hashCode(): Int = map.## * 37 + definition.## * 7

    override def toString: String = s"Road(map ${map.##}, $definition)"
  }

  class Crossing(private val map: RoadMap, private val definition: CrossingDef) {

    def roads: List[Road] = map.roadMap.getOrElse(definition.name, List.empty)

    def reverseRoads: List[Road] = map.reverseRoadMap.getOrElse(definition.name, List.empty)

    def name: String = definition.name

    def coordinates: Coordinates = definition.coordinates

    override def equals(obj: scala.Any): Boolean = obj match {
      case that: Crossing => (this.map eq that.map) && this.definition == that.definition
      case _ => false
    }

    override def hashCode(): Int = map.## * 37 + definition.## * 7

    override def toString: String = s"Crossing(map ${map.##}, $definition)"
  }

  case class CrossingDef(name: String, coordinates: Coordinates)

  case class RoadDef(start: String, end: String, bendingPoints: List[Coordinates])


}
