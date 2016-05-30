package client.js.model

object Statistics {
  val empty: Statistics = new Statistics(List.empty)
}

class Statistics private(val times: List[Double]) {

  def withPoint(time: Double): Statistics = {
    new Statistics(times :+ time)
  }
}
