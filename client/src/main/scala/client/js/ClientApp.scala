package client.js

import autowire._
import org.scalajs.dom
import shared.MapApi
import shared.car.CarsList
import upickle.Js
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}
import scalatags.JsDom.all._

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
    val mainView = new MainView

    dom.document.body.appendChild(mainView.view())
    val mapViewer = new MapViewer(mainView.context())

    ClientApi[MapApi].map().call().onComplete {
      case Success(mapFromServer) => {
        mapViewer.drawMap(mapFromServer)
      }
      case Failure(fail) => println(s"unable to fetch map: $fail")
    }
    createWebSocket("ws://localhost:9000/sim", (e: dom.MessageEvent) => {
      mapViewer.drawCars(read[CarsList](e.data.toString))
    })

  }

  def createWebSocket(address: String, onMessage: dom.MessageEvent => Unit): dom.WebSocket = {
    val webSocket = new dom.WebSocket(address)
    webSocket.onopen = { (e: dom.Event) =>
      webSocket.send("carTest")
    }
    webSocket.onmessage = onMessage
    webSocket
  }
}
