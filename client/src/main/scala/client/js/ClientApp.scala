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
    dom.document.body.appendChild(MainView.view())

    // FIXME lot of ugly fixing

    ClientApi[MapApi].map().call().onComplete {
      case Success(mapFromServer) => {
        val mapViewer = new MapViewer(MainView.context(), mapFromServer)
        createWebSocket("ws://localhost:9000/sim", (e: dom.MessageEvent) => {
          mapViewer.drawCars(read[CarsList](e.data.toString))
        })
      }
      case Failure(fail) => println(s"unable to fetch map: $fail")
    }

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
