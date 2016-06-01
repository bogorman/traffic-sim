package client.js.model

class StatisticsList(private val maxChunksCount: Int) {
  private var statisticsDataList: List[Statistics] = List.empty
  private var currentItemsPerChunk = 1

  def newStatistics(): Unit = {
    statisticsDataList = statisticsDataList :+ Statistics.empty("red")
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
