package client.js

import com.karasiq.bootstrap.BootstrapAttrs._
import org.scalajs.dom.raw.{CanvasRenderingContext2D, Node}

import scalatags.JsDom.all._

object MainView {

  val simulationMap = canvas(
    id := "mapCanvas",
    "width".attr := 1000,
    "height".attr := 1000,
    style := "text-align: center"
  ).render

  val crossingStrategyItems = List(
    "FIFO",
    "Super")

  val crossingStrategyDropdown = div(`class` := "dropdown",
    button(`class` := "btn btn-default dropdown-toggle", `type` := "button", id := "dropdownMenu1",
      `data-toggle` := "dropdown", aria.haspopup := "true", aria.expanded := "true")("Dropdown"),
    ul(`class` := "dropdown-menu", aria.labelledby := "dropdownMenu1",
      crossingStrategyItems.map(li(_)))
  )

  def view(): Node = div(
    h1("Map simulation", style := "text-align: center"),
    simulationMap,
    crossingStrategyDropdown,
    h1("Map simulation", style := "text-align: center"),
    h1("Map simulation", style := "text-align: center"),
    h1("Map simulation", style := "text-align: center")
  ).render

  def context(): CanvasRenderingContext2D = {
    simulationMap.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  }
}