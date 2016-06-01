package client.js.model

import scala.collection.mutable

object Statistics {
  def empty(colorHex: String): Statistics = new Statistics(colorHex)
}

class Statistics private(val colorHex: String) {
  private val dataList: mutable.ListBuffer[Double] = new mutable.ListBuffer
  private var chunkedDataList: mutable.ListBuffer[Double] = new mutable.ListBuffer

  def dataSize: Int = dataList.size

  def maxValue: Double = chunkedDataList.max

  def foreachChunk(consumer: (Double, Int) => Unit): Unit = {
    chunkedDataList.indices.foreach { i =>
      consumer(chunkedDataList(i), i)
    }
  }

  def recalculateLastChunk(currentItemsPerChunk: Int): Unit = {
    val itemsInPreviousChunks = (dataList.size / currentItemsPerChunk) * currentItemsPerChunk
    val itemsInLastChunk = dataList.size % currentItemsPerChunk

    val lastChunkValue = dataList.drop(itemsInPreviousChunks).sum / itemsInLastChunk

    chunkedDataList.remove(chunkedDataList.size - 1)
    chunkedDataList.append(lastChunkValue)
  }

  def shrink(currentItemsPerChunk: Int): Unit = {
    chunkedDataList = chunkedDataList.grouped(2).map(_.sum).map(_ / 2).to[mutable.ListBuffer]

    if (dataList.size % currentItemsPerChunk != 0) {
      recalculateLastChunk(currentItemsPerChunk)
    }
  }

  def addPoint(item: Double, currentItemsPerChunk: Int): Unit = {
    dataList.append(item)

    val itemsInLastChunk = dataList.size % currentItemsPerChunk
    if (itemsInLastChunk == 0) {
      chunkedDataList.append(item)
    } else {
      recalculateLastChunk(currentItemsPerChunk)
    }
  }
}
