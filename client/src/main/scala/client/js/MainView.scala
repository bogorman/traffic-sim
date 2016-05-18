package client.js

import org.scalajs.dom.raw.{CanvasRenderingContext2D, Node}

import scalatags.JsDom.all._

class MainView {
  val simulationMap = canvas(id := "mapCanvas", "width".attr := 1000, "height".attr := 1000).render

  def view(): Node = div(
    h1("Map simulation"),
    simulationMap
  ).render

  def context(): CanvasRenderingContext2D = {
    simulationMap.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  }
}