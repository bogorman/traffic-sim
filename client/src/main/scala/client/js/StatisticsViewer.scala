package client.js

import client.js.model.Statistics
import org.scalajs.dom.raw.CanvasRenderingContext2D

class StatisticsViewer(context: CanvasRenderingContext2D) {
  val ChartArea = 400
  val Margins = 50

  def drawStatistics(statistics: Statistics): Unit = {
    context.fillRect(Margins, Margins, ChartArea, ChartArea)

    val maxY = statistics.times.max

    if (statistics.times.size <= ChartArea) {
      statistics.times.indices foreach {
        x => {
          val y = (ChartArea * statistics.times(x) / maxY).toInt
          drawPoint(x + Margins, Margins + ChartArea - y)
        }
      }
    }
  }

  def drawPoint(x: Int, y: Int): Unit = {
    context.beginPath()
    context.moveTo(x + Margins, y + Margins)
    context.lineTo(x + Margins, y + Margins)
    context.closePath()
    context.stroke()
  }
}
