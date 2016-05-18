package client.js

import autowire._
import org.scalajs.dom
import shared.MapApi
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

    ClientApi[MapApi].map().call().onComplete {
      case Success(mapFromServer) => {
        new MapViewer(MainView.context()).drawMap(mapFromServer)
      }
      case Failure(fail) => println(s"unable to fetch map: $fail")
    }
    createWebSocket("ws://localhost:9000/sim")
  }

  def createWebSocket(address: String): dom.WebSocket = {
    val webSocket = new dom.WebSocket(address)
    webSocket.onopen = { (e: dom.Event) =>
      webSocket.send("carTest")
    }
    webSocket.onmessage = { (e: dom.MessageEvent) =>
      println(e.data.toString)
      //      dom.document.getElementById("websocketMessages").appendChild(li(e.data.toString).render)
    }
    webSocket
  }
}
