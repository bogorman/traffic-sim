package client.js.model

class StatisticsList(private val maxChunksCount: Int) {
  private var statisticsDataList: List[Statistics] = List.empty
  private var currentItemsPerChunk = 1
  private val colorsList = Seq("red", "blue", "green", "yellow", "cyan", "orange", "magenta", "brown", "black", "grey")
  private var colorCounter = 0

  private def nextColor: String = {
    val color = colorsList(colorCounter)
    colorCounter = (colorCounter + 1) % colorsList.length
    color
  }

  def newStatistics(): String = {
    val color = nextColor
    statisticsDataList = statisticsDataList :+ Statistics.empty(color)
    color
  }

  def maxValue: Double = statisticsDataList.map(_.maxValue).max

  def foreachStatistics(consumer: Statistics => Unit): Unit = {
    statisticsDataList foreach consumer
  }

  def addPoint(data: Double): Unit = {
    if (statisticsDataList.nonEmpty) {
      statisticsDataList.last.addPoint(data, currentItemsPerChunk)

      if (statisticsDataList.last.dataSize > currentItemsPerChunk * maxChunksCount) {
        currentItemsPerChunk *= 2
        statisticsDataList foreach (_.shrink(currentItemsPerChunk))
      }
    }
  }
}
