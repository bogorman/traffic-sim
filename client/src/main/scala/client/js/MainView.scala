package client.js

import org.scalajs.dom
import org.scalajs.dom.raw.CanvasRenderingContext2D

import scalatags.JsDom.all._

class MainView {
  val simulationMap = createCanvas(1000)

  val statisticsChart = createCanvas(500)

  def createCanvas(size: Int) = canvas(
    "width".attr := size,
    "height".attr := size
  ).render

  val simulationMapContext = simulationMap.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  val statisticsChartContext = statisticsChart.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  // todo: those values & other simulation parameters should be extracted to shared package
  val lightStrategies = List(
    "No lights - first in, first out",
    "No lights - right-of-way",
    "Constant light change time",
    "Random light change time",
    "Load balanced light change time")

  val lightsStrategyDropdown =
    select(`class` := "form-control", id := "lightsStrategyDropdown")(
      lightStrategies.map(option(_))
    ).render

  val numOfCarsInput =
    input(`type` := "number", `class` := "form-control", id := "numOfCarsInput", value := 50)
      .render

  val numOfCrossingsInput =
    input(`type` := "number", `class` := "form-control", id := "numOfCrossingsInput", value := 50)
      .render

  val submitButton =
    div(`class` := "btn-group btn-group-justified", role := "group", marginTop := "15px")(
      div(`class` := "btn-group", role := "group")(
        button(`type` := "button", `class` := "btn btn-primary")("Restart simulation")
      )
    ).render

  // todo: inject callback from ClientApp and send it to server
  submitButton.onclick = (e: dom.MouseEvent) => {
    println("pushed form button")
    println(lightsStrategyDropdown.value)
    println(numOfCarsInput.value)
    println(numOfCrossingsInput.value)
  }

  val wholePage =
    div(`class` := "container-fluid", marginTop := "20px")(
      div(`class` := "row")(
        div(`class` := "col-md-8")(
          div(`class` := "panel panel-primary")(
            div(`class` := "panel-heading")("Traffic simulation"),
            div(`class` := "panel-body")(
              simulationMap)
          )
        ),
        div(`class` := "col-md-4")(
          div(`class` := "panel panel-info")(
            div(`class` := "panel-heading")("Settings"),
            div(`class` := "panel-body")(
              div(`class` := "form-group")(
                label(`for` := "lightsStrategyDropdown")("Traffic lights management strategy"),
                lightsStrategyDropdown
              ),
              div(`class` := "input-group", width := "100%")(
                label(`for` := "numOfCarsInput")("Number of vehicles in simulation"),
                numOfCarsInput
              ),
              div(`class` := "input-group", width := "100%")(
                label(`for` := "numOfCrossingsInput")("Number of crossings in simulation"),
                numOfCrossingsInput
              ),
              submitButton
            )
          ),
          div(`class` := "panel panel-success")(
            div(`class` := "panel-heading")("Statistics"),
            div(`class` := "panel-body")(
              p("Lorem Ipsum")
            )
          )
        )
      )
    ).render

}