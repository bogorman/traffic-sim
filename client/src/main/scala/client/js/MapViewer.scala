package client.js

import org.scalajs.dom.raw.CanvasRenderingContext2D
import shared.geometry._
import shared.map.RoadMap

object MapViewer {
  private val MapCoordinatesRange = 1000.0
  private val PixelsMapRange = 800.0
  private val PixelsForMargins = 50.0

  private val PixelsPerMapStep = PixelsMapRange / MapCoordinatesRange

  private val HalfCrossingSize = 20.0

  def drawMap(context: CanvasRenderingContext2D, map: RoadMap): Unit = {
    context.font = HalfCrossingSize + "px Arial"

    map.crossings.foreach(crossing => {
      drawCrossing(context, crossing.coordinates.x, crossing.coordinates.y, crossing.name)
    })

    map.roads.foreach(road => {
      val allPoints = road.start.coordinates :: road.bendingPoints ::: road.end.coordinates :: List.empty
      allPoints.sliding(2).foreach { case List(start, end) => drawRoad(context, start, end) }
    })

    context.stroke
  }

  def drawCrossing(context: CanvasRenderingContext2D, x: Double, y: Double, name: String): Unit = {
    val scaledX = PixelsForMargins + x * PixelsPerMapStep
    val scaledY = PixelsForMargins + y * PixelsPerMapStep

    context.beginPath
    context.arc(scaledX, scaledY, HalfCrossingSize, 0, 2 * Math.PI)
    context.closePath
    context.fillStyle = "#803CA2"
    context.fill
    context.stroke

    context.fillStyle = "#000000"

    context.fillText(name, scaledX + 1.5 * HalfCrossingSize, scaledY - 0.5 * HalfCrossingSize)
  }

  def drawRoad(context: CanvasRenderingContext2D, start: Coordinates, end: Coordinates): Unit = {
    val scaledStartX = PixelsForMargins + start.x * PixelsPerMapStep
    val scaledStartY = PixelsForMargins + start.y * PixelsPerMapStep
    val scaledEndX = PixelsForMargins + end.x * PixelsPerMapStep
    val scaledEndY = PixelsForMargins + end.y * PixelsPerMapStep

    context.moveTo(scaledStartX, scaledStartY)
    context.lineTo(scaledEndX, scaledEndY)
  }
}

