package client.js.model

object Statistics {
  val empty: Statistics = new Statistics(List.empty)
}

class Statistics private(val times: List[Double]) {
  def normalized(maxItems: Int): Statistics = {
    if (times.size <= maxItems) {
      this
    } else {
      val newTimes = (0 until maxItems).map(index => {
        val start = index * times.size / maxItems
        val end = (index + 1) * times.size / maxItems

        val sublist = (start until end).map(times(_)).toList
        sublist.sum / sublist.size
      }).toList
      new Statistics(newTimes)
    }
  }


  def withPoint(time: Double): Statistics = {
    new Statistics(times :+ time)
  }
}
