package client.js

import client.js.model.StatisticsList
import org.scalajs.dom.raw.CanvasRenderingContext2D
import shared.geometry._

import scala.math._

class StatisticsViewer(context: CanvasRenderingContext2D) {
  val ChartArea = 500
  val Margins = 20

  def drawStatistics(statisticsList: StatisticsList): Unit = {
    context.clearRect(0, 0, ChartArea + 2 * Margins, ChartArea + 2 * Margins)

    context.strokeStyle = "black"

    drawArrow(Margins - 1 >< Margins + ChartArea + 1, Margins - 1 >< Margins / 4)
    drawArrow(Margins - 1 >< Margins + ChartArea + 1, Margins + ChartArea + Margins * 3 / 4 >< Margins + ChartArea + 1)

    val maxY = statisticsList.maxValue

    statisticsList foreachStatistics { statistics =>
      context.strokeStyle = statistics.colorHex

      statistics.foreachChunk { (chunkValue, index) =>
        val y = (ChartArea * chunkValue / maxY).toInt
        drawPoint(index >< ChartArea - y)
      }
    }
  }

  private def drawPoint(location: Coordinates): Unit = {
    context.strokeRect(location.x + Margins, location.y + Margins, 1, 1)
  }

  private def rotatedPoint(point: Coordinates, center: Coordinates, radiansToRight: Double): Coordinates = {
    val sine = sin(radiansToRight)
    val cosine = cos(radiansToRight)

    (point.x - center.x) * cosine - (point.y - center.y) * sine + center.x ><
      (point.x - center.x) * sine + (point.y - center.y) * cosine + center.y
  }

  private def movedPoint(point: Coordinates, direction: Coordinates): Coordinates = {
    val vector = direction.x - point.x >< direction.y - point.y
    val vectorLength = sqrt(pow(vector.x, 2) + pow(vector.y, 2))
    val normalizedVector = vector.x / vectorLength >< vector.y / vectorLength
    point.x + normalizedVector.x * 20 >< point.y + normalizedVector.y * 20
  }

  private def drawArrow(start: Coordinates, end: Coordinates): Unit = {
    drawLine(start, end)
    drawLine(movedPoint(end, rotatedPoint(start, end, +Pi / 6)), end)
    drawLine(movedPoint(end, rotatedPoint(start, end, -Pi / 6)), end)
  }

  private def drawLine(start: Coordinates, end: Coordinates): Unit = {
    context.beginPath
    context.moveTo(start.x, start.y)
    context.lineTo(end.x, end.y)
    context.closePath
    context.stroke
  }
}
