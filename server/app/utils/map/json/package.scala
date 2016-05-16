package utils.map

import play.api.libs.json.Reads._
import play.api.libs.json._
import shared.map._
import shared.geometry._

package object json {

  object CrossingReads extends Reads[CrossingDef] {
    override def reads(node: JsValue): JsResult[CrossingDef] = (for {
      name <- (node \ "name").asOpt[String]
      coordinates <- node.asOpt(CoordinatesReads)
    } yield CrossingDef(name, coordinates)) map { JsSuccess(_) } getOrElse JsError()
  }

  object RoadReads extends Reads[Seq[RoadDef]] {
    override def reads(node: JsValue): JsResult[Seq[RoadDef]] = {
      val roadsOption = for {
        start <- (node \ "start").asOpt[String]
        end <- (node \ "end").asOpt[String]
        bendNodes = (node \ "bends").asOpt(list(CoordinatesReads)) getOrElse Nil
        oneway = (node \ "oneway").asOpt[Boolean] getOrElse false
      } yield {
        if (oneway)
          RoadDef(start, end, bendNodes) :: Nil
        else
          RoadDef(start, end, bendNodes) :: RoadDef(end, start, bendNodes.reverse) :: Nil
      }

      roadsOption map {JsSuccess(_)} getOrElse JsError()
    }
  }

  object CoordinatesReads extends Reads[Coordinates] {
    override def reads(node: JsValue): JsResult[Coordinates] = (for {
      x <- (node \ "x").asOpt[Double]
      y <- (node \ "y").asOpt[Double]
    } yield x >< y) map { JsSuccess(_) } getOrElse JsError()
  }

  implicit object MapReads extends Reads[RoadMap] {
    override def reads(node: JsValue): JsResult[RoadMap] = (for {
      crossings <- (node \ "crossings").asOpt(list(CrossingReads))
      roads <- (node \ "roads").asOpt(list(RoadReads))
      crossingsGrouped: Map[String, List[CrossingDef]] = crossings groupBy {_.name}
      roadsFlatten = roads.flatten
      if crossingsGrouped forall { case (_, l) => l.size == 1 }
      if roadsFlatten forall { case RoadDef(start, end, _) => crossingsGrouped.contains(start) && crossingsGrouped.contains(end) }
    } yield new RoadMap(crossings, roadsFlatten)) map { JsSuccess(_) } getOrElse JsError()
  }
}
