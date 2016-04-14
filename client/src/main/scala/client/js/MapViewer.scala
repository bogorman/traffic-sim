package client.js

import org.scalajs.dom
import org.scalajs.dom.raw.CanvasRenderingContext2D

import scala.scalajs.js

object MapViewer extends js.JSApp {

  def main(): Unit = {

    dom.document.getElementById("visualizationHeader").textContent = "Visualization"

    val context = dom.document.getElementById("mapCanvas").asInstanceOf[dom.html.Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    val range = 4
    val normalization = 800
    val margins = 50

    drawCrossing(context, 0, 0, "A", range, normalization, margins)
    drawCrossing(context, 0, 2, "D", range, normalization, margins)
    drawCrossing(context, 2, 0, "B", range, normalization, margins)
    drawCrossing(context, 2, 2, "C", range, normalization, margins)
    drawCrossing(context, 4, 2, "E", range, normalization, margins)
    drawCrossing(context, 2, 3, "", range, normalization, margins)

    drawRoad(context, 0, 0, 0, 2, range, normalization, margins)
    drawRoad(context, 0, 2, 0, 0, range, normalization, margins)
    drawRoad(context, 2, 0, 0, 0, range, normalization, margins)
    drawRoad(context, 0, 0, 2, 0, range, normalization, margins)
    drawRoad(context, 2, 0, 2, 2, range, normalization, margins)
    drawRoad(context, 2, 2, 2, 0, range, normalization, margins)
    drawRoad(context, 0, 2, 2, 3, range, normalization, margins)
    drawRoad(context, 2, 3, 0, 2, range, normalization, margins)
    drawRoad(context, 4, 2, 2, 2, range, normalization, margins)
    drawRoad(context, 4, 2, 2, 0, range, normalization, margins)
    drawRoad(context, 4, 2, 2, 3, range, normalization, margins)
    drawRoad(context, 2, 3, 4, 2, range, normalization, margins)
  }

  def drawCrossing(context: CanvasRenderingContext2D, x: Int, y: Int, name: String, range: Int, normalization: Int, margins: Int): Unit = {
    val pixelsPerStep = normalization / range
    val halfCrossingSize = pixelsPerStep / 50
    val scaledX = margins + x * pixelsPerStep
    val scaledY = margins + y * pixelsPerStep

    context.fillRect(scaledX - halfCrossingSize, scaledY - halfCrossingSize, 2 * halfCrossingSize, 2 * halfCrossingSize)

    context.font = 20 * halfCrossingSize + "px Arial"
    context.fillText(name, scaledX + halfCrossingSize, scaledY + 20 * halfCrossingSize, 20 * halfCrossingSize)

    context stroke
  }

  def drawRoad(context: CanvasRenderingContext2D, startX: Int, startY: Int, endX: Int, endY: Int, range: Int, normalization: Int, margins: Int): Unit = {
    val pixelsPerStep = normalization / range
    val scaledStartX = margins + startX * pixelsPerStep
    val scaledStartY = margins + startY * pixelsPerStep
    val scaledEndX = margins + endX * pixelsPerStep
    val scaledEndY = margins + endY * pixelsPerStep

    context.moveTo(scaledStartX, scaledStartY)
    context.lineTo(scaledEndX, scaledEndY)

    context stroke
  }
}

