package client.js

import client.js.model.StatisticsList
import org.scalajs.dom
import org.scalajs.dom.{Event, MessageEvent}
import shared.map.{CarsUpdate, RoadMap, SocketMessage}
import shared.simulation.parameters.{CustomEnumerationSerialization, SimulationParameters}
import upickle.default._

import scala.scalajs.js

object ClientApp extends js.JSApp with CustomEnumerationSerialization {
  def main(): Unit = {
    val mainView = new MainView()
    dom.document.body.appendChild(mainView.wholePage)

    val webSocket = new dom.WebSocket("ws://localhost:9000/sim")
    val curriedSendParamsFun = sendSimulationParamsFun(webSocket) _
    webSocket.onopen = (e: Event) => curriedSendParamsFun(SimulationParameters.default)
    mainView.onFormSubmit(curriedSendParamsFun)

    var mapViewer = Option.empty[MapViewer]
    val statisticsViewer = new StatisticsViewer(mainView.statisticsChartContext)
    val statisticsList = new StatisticsList(statisticsViewer.ChartArea)
    webSocket.onmessage = (e: MessageEvent) => {
      read[SocketMessage](e.data.toString) match {
        case update: CarsUpdate =>
          update.stats foreach {
            stat => statisticsList.addPoint(stat)
          }
          mapViewer.foreach {
            m => m.drawCars(update)
          }
          statisticsViewer.drawStatistics(statisticsList)

        case mapFromServer: RoadMap =>
          statisticsList.newStatistics()
          mapViewer = Option(new MapViewer(mainView.simulationMapContext, mapFromServer))

        case _ => println("socket msg parsing error!")
      }
    }
  }

  def sendSimulationParamsFun(socket: dom.WebSocket)(simulationParameters: SimulationParameters): Unit = {
    socket.send(write(simulationParameters))
  }

}
