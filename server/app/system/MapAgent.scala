package system

import java.io.FileInputStream

import akka.actor.Actor
import de.ummels.prioritymap.PriorityMap
import play.api.libs.json.Json
import shared.map._
import system.MapAgent._
import utils.map.json.MapReads


object MapAgent {

  sealed trait RouteResponse

  case class ShortestRouteRequest(start: String, end: String)

  case class Route(roads: Seq[Road]) extends RouteResponse

  case object GetMap

  case object Unreachable extends RouteResponse

  case object UnknownNodes extends RouteResponse

  def dijkstra(map: RoadMap, start: Crossing, end: Crossing): RouteResponse = {

    @scala.annotation.tailrec
    def work(priorityMap: PriorityMap[Crossing, Double], unvisited: Set[Crossing], leadingRoads: Map[Crossing, Road]): RouteResponse = {
      val worked: Option[(Crossing, PriorityMap[Crossing, Double], Map[Crossing, Road])] = priorityMap.firstKey map { current =>
        val priority = priorityMap(current)
        val roadsToVisit = current.roads filter { r => unvisited(r.end) }
        val (newPMap, newRMap) = roadsToVisit.foldLeft(priorityMap -> leadingRoads) {
          case ((pMap, rMap), road) =>
            val newPriority = road.length + priority
            val oldPriority = pMap.getOrElse(road.end, Double.PositiveInfinity)
            if (newPriority < oldPriority) (pMap + (road.end -> newPriority), rMap + (road.end -> road))
            else (pMap, rMap)
        }
        (current, newPMap, newRMap)
      }

      worked match {
        case Some((`end`, _, rMap)) =>
          val roads: Stream[Road] = Stream.iterate(Option(rMap(end))) {_.flatMap(r => rMap.get(r.start))} takeWhile {_.isDefined} map {_.get}
          Route(roads.toList.reverse)
        case Some((current, pMap, rMap)) => work(pMap - current, unvisited - current, rMap)
        case _ => Unreachable
      }
    }

    work(PriorityMap(start -> 0), map.crossings.toSet, Map())
  }
}

class MapAgent extends Actor {

  lazy val map = {
    val result: RoadMap = Json.parse(new FileInputStream("map.json")).as[RoadMap]
    result
  }

  override def receive: Receive = {
    case GetMap => sender ! map

    case ShortestRouteRequest(startName, endName) =>
      val result = for {
        start <- map.crossingsMap.get(startName)
        end <- map.crossingsMap.get(endName)
      } yield dijkstra(map, start, end)
      sender ! (result getOrElse UnknownNodes)
  }

}
