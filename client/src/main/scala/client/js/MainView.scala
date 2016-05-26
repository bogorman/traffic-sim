package client.js

import client.js.view.widget.{Dropdown, Spinner}
import org.scalajs.dom.raw.{CanvasRenderingContext2D, Node}

import scalatags.JsDom.all._

object MainView {

  val simulationMap = canvas(
    "width".attr := 1000,
    "height".attr := 1000
  ).render

  val crossingStrategies = List(
    "FIFO",
    "Super")

  val crossingStrategyDropdown = new Dropdown(crossingStrategies)

  val picker = new Spinner(1000)

  def view(): Node = div(
    h1("Map simulation", style := "text-align: center"),
    simulationMap,
    crossingStrategyDropdown root,
    picker root
  ).render

  def context(): CanvasRenderingContext2D = {
    simulationMap.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  }
}