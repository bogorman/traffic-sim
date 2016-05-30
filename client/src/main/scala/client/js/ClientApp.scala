package client.js

import autowire._
import client.js.model.Statistics
import org.scalajs.dom
import org.scalajs.dom.MessageEvent
import shared.MapApi
import shared.car.CarsList
import upickle.Js
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Random, Success}

object ClientApi extends Client[Js.Value, Reader, Writer] {
  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)

  override def doCall(req: Request): Future[Js.Value] = {
    dom.ext.Ajax.get(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.json.write(Js.Obj(req.args.toSeq: _*))
    ).map(_.responseText)
      .map(upickle.json.read)
  }
}

object ClientApp extends js.JSApp {
  def main(): Unit = {
    val mainView = new MainView()
    dom.document.body.appendChild(mainView.wholePage)

    ClientApi[MapApi].map().call().onComplete {
      case Success(mapFromServer) =>
        val mapViewer = new MapViewer(mainView.simulationMapContext, mapFromServer)
        val statisticsViewer = new StatisticsViewer(mainView.statisticsChartContext)

        var statistics = Statistics.empty

        (0 until 1000) foreach {
          x => {
            statistics = statistics.withPoint(Random.nextDouble())
          }
        }

        val webSocket = new dom.WebSocket("ws://localhost:9000/sim")
        webSocket.onmessage = (e: MessageEvent) => {
          mapViewer.drawCars(read[CarsList](e.data.toString))
          statisticsViewer.drawStatistics(statistics)
        }
      case Failure(fail) => println(s"unable to fetch map: $fail")
    }
  }
}
