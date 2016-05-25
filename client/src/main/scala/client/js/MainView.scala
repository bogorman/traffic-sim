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

  def out(): Unit = {
    println("a")
  }

  val crossingStrategyDropdown = div(`class` := "dropdown",
    button(`class` := "btn btn-default dropdown-toggle", `type` := "button", id := "dropdownMenu1",
      `data-toggle` := "dropdown", aria.haspopup := "true", aria.expanded := "true",
      onclick := "$(\"ul\").toggleClass(\"displayStyle\")", span()("Dropdown")),
    ul(`class` := "dropdown-menu", aria.labelledby := "dropdownMenu1",
      crossingStrategyItems.map(name => li(name, onclick := "$(\"button\").find(\">:first-child\").replaceWith(\"<span>" + name + "</span>\")")))
  )

  val picker = div(`class` := "input-group",
    div(`class` := "input-group-addon fixed-btn",
      button(`class` := "btn btn-default", onclick := "$(\"#spinner1\").val(function(i, value){return Math.max(parseInt(value) - 1, 0)})",
        span(`class` := "caret"))),
    input(id := "spinner1", `class` := "form-control", value := "0"),
    div(`class` := "input-group-addon fixed-btn",
      button(`class` := "btn btn-default", onclick := "$(\"#spinner1\").val(function(i, value){return parseInt(value) + 1})",
        span(`class` := "caret caret-reversed")))
  )

  def view(): Node = div(
    h1("Map simulation", style := "text-align: center"),
    simulationMap,
    crossingStrategyDropdown,
    picker,
    h1("Map simulation", style := "text-align: center"),
    h1("Map simulation", style := "text-align: center"),
    h1("Map simulation", style := "text-align: center")
  ).render

  def context(): CanvasRenderingContext2D = {
    simulationMap.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  }
}