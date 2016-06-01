package client.js

import client.js.model.StatisticsList
import org.scalajs.dom.raw.CanvasRenderingContext2D

class StatisticsViewer(context: CanvasRenderingContext2D) {
  val ChartArea = 500
  val Margins = 20

  def drawStatistics(statisticsList: StatisticsList): Unit = {
    context.clearRect(0, 0, ChartArea + 2 * Margins, ChartArea + 2 * Margins)

    val maxY = statisticsList.maxValue

    statisticsList foreachStatistics { statistics =>
      context.strokeStyle = statistics.colorHex

      statistics.foreachChunk { (chunkValue, index) =>
        val y = (ChartArea * chunkValue / maxY).toInt
        drawPoint(index, ChartArea - y)
      }
    }
  }

  private def drawPoint(x: Int, y: Int): Unit = {
    context.strokeRect(x + Margins, y + Margins, 1, 1)
  }
}
