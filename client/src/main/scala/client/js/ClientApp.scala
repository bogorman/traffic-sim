package client.js

import client.js.model.StatisticsList
import org.scalajs.dom
import org.scalajs.dom.{Event, MessageEvent}
import shared.map.{CarsUpdate, RoadMap, SocketMessage}
import shared.simulation.parameters.SimulationParameters
import upickle.default._

import scala.scalajs.js

object ClientApp extends js.JSApp {
  def main(): Unit = {
    val mainView = new MainView()
    dom.document.body.appendChild(mainView.wholePage)

    val webSocket = new dom.WebSocket("ws://localhost:9000/sim")
    webSocket.onopen = (e: Event) => {
      webSocket.send(write(SimulationParameters.default))
    }

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
            c => c.drawCars(update)
          }
          statisticsViewer.drawStatistics(statisticsList)

        case mapFromServer: RoadMap =>
          println(mapFromServer)
          statisticsList.newStatistics()
          mapViewer = Option(new MapViewer(mainView.simulationMapContext, mapFromServer))

        case _ => println("cos innego, czego nie rozumiemy")
      }
    }
  }

}
