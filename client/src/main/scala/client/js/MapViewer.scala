package client.js

import org.scalajs.dom.raw.CanvasRenderingContext2D
import shared.geometry._
import shared.map.{Car, Road, RoadMap}

import scala.math._

class MapViewer(context: CanvasRenderingContext2D, map: RoadMap) {
  private val MapCoordinatesRange = 1000.0
  private val PixelsMapRange = 800.0
  private val PixelsForMargins = 100.0

  private val PixelsPerMapStep = PixelsMapRange / MapCoordinatesRange

  private val HalfCrossingSize = 15.0
  private val HalfCarSize = 5.0

  private val ColorPimpPurple = "#803CA2"
  private val ColorBlack = "#000000"

  def drawMap(): Unit = {
    context.font = HalfCrossingSize + "px Arial"

    map.crossings.foreach(crossing => {
      drawCrossing(crossing.coordinates, crossing.name)
    })

    map.roads.foreach(drawRoad)
  }

  private def drawRoad(road: Road): Unit = {
    if (road.bendingPoints.isEmpty) {
      val roadStart = movedPoint(scaleCoordinates(road.start.coordinates), scaleCoordinates(road.end.coordinates), HalfCrossingSize)
      val roadEnd = movedPoint(scaleCoordinates(road.end.coordinates), scaleCoordinates(road.start.coordinates), HalfCrossingSize)
      drawArrow(roadStart, roadEnd)
    } else {
      val roadStart = movedPoint(scaleCoordinates(road.start.coordinates), scaleCoordinates(road.bendingPoints.head), HalfCrossingSize)
      val roadEnd = movedPoint(scaleCoordinates(road.end.coordinates), scaleCoordinates(road.bendingPoints.last), HalfCrossingSize)
      drawLine(roadStart, scaleCoordinates(road.bendingPoints.head))
      drawArrow(scaleCoordinates(road.bendingPoints.last), roadEnd)
      if (road.bendingPoints.size > 1) {
        road.bendingPoints.map(scaleCoordinates).sliding(2).foreach { case List(start, end) => drawLine(start, end) }
      }
    }
  }

  private def rotatedPoint(point: Coordinates, center: Coordinates, radiansToRight: Double): Coordinates = {
    val sine = sin(radiansToRight)
    val cosine = cos(radiansToRight)

    (point.x - center.x) * cosine - (point.y - center.y) * sine + center.x ><
      (point.x - center.x) * sine + (point.y - center.y) * cosine + center.y
  }

  private def movedPoint(point: Coordinates, direction: Coordinates, distance: Double): Coordinates = {
    val vector = direction.x - point.x >< direction.y - point.y
    val vectorLength = sqrt(pow(vector.x, 2) + pow(vector.y, 2))
    val normalizedVector = vector.x / vectorLength >< vector.y / vectorLength
    point.x + normalizedVector.x * distance >< point.y + normalizedVector.y * distance
  }

  private def drawCrossing(location: Coordinates, name: String): Unit = {
    val textX = scaleValue(location.x) + 1.5 * HalfCrossingSize
    val textY = scaleValue(location.y) - 1.0 * HalfCrossingSize

    drawCircle(scaleCoordinates(location), ColorPimpPurple, HalfCrossingSize)
    context.fillText(name, textX, textY)
  }

  def drawCars(carsList: List[Car]): Unit = {
    // FIXME ugly temporary fix
    context.clearRect(0, 0, 1000, 1000)
    drawMap()
    carsList.foreach(car => drawCar(car.location, car.previousLocation, car.hexColor))
  }

  private def drawCar(location: Coordinates, previousLocation: Option[Coordinates], color: String): Unit = {
    val scaledCoordinates = scaleCoordinates(location)
    if (previousLocation.isEmpty) {
      drawRect(scaledCoordinates, color, HalfCarSize)
    } else {
      val scaledPreviousLocation = scaleCoordinates(previousLocation.get)
      val movingDirection = scaledCoordinates.x - scaledPreviousLocation.x >< scaledCoordinates.y - scaledPreviousLocation.y
      val movingDirectionLength = sqrt(pow(movingDirection.x, 2) + pow(movingDirection.y, 2))
      val unitMovingDirection = movingDirection.x / movingDirectionLength * HalfCarSize >< movingDirection.y / movingDirectionLength * HalfCarSize
      val rotatedMovingDirection = -unitMovingDirection.y >< unitMovingDirection.x
      val movedLocation = scaledCoordinates.x + rotatedMovingDirection.x >< scaledCoordinates.y + rotatedMovingDirection.y
      drawRect(movedLocation, color, HalfCarSize)
    }
  }

  private def drawCircle(middle: Coordinates, color: String, radius: Double): Unit = {
    context.fillStyle = color

    context.beginPath
    context.arc(middle.x, middle.y, radius, 0, 2 * Math.PI)
    context.closePath
    context.fill
    context.stroke

    context.fillStyle = ColorBlack
  }

  private def drawRect(middle: Coordinates, color: String, halfRectSide: Double): Unit = {
    val rectX = middle.x - halfRectSide
    val rectY = middle.y - halfRectSide
    val rectSide = 2 * halfRectSide

    context.fillStyle = color

    context.fillRect(rectX, rectY, rectSide, rectSide)
    context.strokeRect(rectX, rectY, rectSide, rectSide)

    context.fillStyle = ColorBlack
  }

  private def drawArrow(start: Coordinates, end: Coordinates): Unit = {
    drawLine(start, end)
    drawLine(movedPoint(end, rotatedPoint(start, end, +Pi / 6), HalfCrossingSize), end)
    drawLine(movedPoint(end, rotatedPoint(start, end, -Pi / 6), HalfCrossingSize), end)
  }

  private def drawLine(start: Coordinates, end: Coordinates): Unit = {
    context.beginPath
    context.moveTo(start.x, start.y)
    context.lineTo(end.x, end.y)
    context.closePath
    context.stroke
  }

  private def scaleCoordinates(coordinates: Coordinates): Coordinates = scaleValue(coordinates.x) >< scaleValue(coordinates.y)

  private def scaleValue(value: Double): Double = PixelsForMargins + value * PixelsPerMapStep
}

