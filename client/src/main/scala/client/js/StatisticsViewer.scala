package client.js

import client.js.model.Statistics
import org.scalajs.dom.raw.CanvasRenderingContext2D

class StatisticsViewer(context: CanvasRenderingContext2D) {
  val ChartArea = 500
  val Margins = 20

  def drawStatistics(statistics: Statistics): Unit = {
    context.clearRect(Margins, Margins, ChartArea, ChartArea)

    val normalizedStatistics = statistics.normalized(ChartArea)

    val maxY = normalizedStatistics.times.max

    normalizedStatistics.times.indices foreach {
      x => {
        val y = (ChartArea * normalizedStatistics.times(x) / maxY).toInt
        drawPoint(x, ChartArea - y)
      }
    }
  }

  def drawPoint(x: Int, y: Int): Unit = {
    context.strokeRect(x + Margins, y + Margins, 1, 1)
  }
}
