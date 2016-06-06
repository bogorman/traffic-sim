package client.js

import org.scalajs.dom
import org.scalajs.dom.raw.CanvasRenderingContext2D
import shared.simulation.parameters._

import scalatags.JsDom.all._

class MainView {
  val simulationMap = createCanvas(1000)

  val statisticsChart = createCanvas(540)

  def createCanvas(size: Int) = canvas(
    "width".attr := size,
    "height".attr := size
  ).render

  val simulationMapContext = simulationMap.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  val statisticsChartContext = statisticsChart.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  def enumToListOfOptions[E <: Enumeration](e: E) = {
    e.values.map(strategy => option(strategy.toString)).toList
  }

  val crossingStrategyDropdown =
    select(`class` := "form-control", id := "lightsStrategyDropdown")(
      enumToListOfOptions(CrossingStrategyEnum)
    ).render

  val numOfCarsInput =
    input(`type` := "number", `class` := "form-control", id := "numOfCarsInput", value := SimulationParameters.default.carsMaxNumber)
      .render

  val mapTypeDropdown =
    select(`class` := "form-control", id := "mapTypeDropdown")(
      enumToListOfOptions(MapFileEnum)
    ).render

  val submitButton =
    div(`class` := "btn-group btn-group-justified", role := "group", marginTop := "15px")(
      div(`class` := "btn-group", role := "group")(
        button(`type` := "button", `class` := "btn btn-primary")("Restart simulation with applied parameters")
      )
    ).render

  val chartDescription =
    ul().render

  def addChartDescription(chartColor: String) = {
    val newDescription = li(
      p(color := chartColor)(currentSimulationParameters.toString)
    ).render
    chartDescription.appendChild(newDescription)
  }

  def currentSimulationParameters: SimulationParameters = {
    new SimulationParameters(
      numOfCarsInput.value.toInt,
      MapFileEnum.withName(mapTypeDropdown.value),
      CrossingStrategyEnum.withName(crossingStrategyDropdown.value)
    )
  }

  def onFormSubmit(callback: SimulationParameters => Unit) = {
    submitButton.onclick = (e: dom.MouseEvent) => {
      callback(currentSimulationParameters)
    }
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
                crossingStrategyDropdown
              ),
              div(`class` := "input-group", width := "100%")(
                label(`for` := "numOfCarsInput")("Number of vehicles in simulation"),
                numOfCarsInput
              ),
              div(`class` := "form-group")(
                label(`for` := "mapTypeDropdown")("Map for simulation"),
                mapTypeDropdown
              ),
              submitButton
            )
          ),
          div(`class` := "panel panel-success")(
            div(`class` := "panel-heading")("Statistics"),
            div(`class` := "panel-body")(
              statisticsChart,
              chartDescription
            )
          )
        )
      )
    ).render

}