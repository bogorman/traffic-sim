package client.js

import autowire._
import org.scalajs.dom
import org.scalajs.dom.raw.CanvasRenderingContext2D
import shared.map.MapApi
import shared.map.RoadMap
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
    val textContent = ClientApi[MapApi].test().call().onComplete {
      case Success(succ) => println(s"success: $succ")
      case Failure(fail) => println(s"failure: $fail")
    }

    val mapCanvas = canvas(id := "mapCanvas", width := 900, height := 900).render
    dom.document.body.appendChild(mapCanvas)
    val context2D = mapCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    MapViewer.drawMap(context2D, new RoadMap(List.empty, List.empty))
  }

  def createWebSocket(address: String): dom.WebSocket = {
    val webSocket = new dom.WebSocket("ws://localhost:9000/socket")
    webSocket.onopen = { (e: dom.Event) =>
      webSocket.send("hello")
    }
    webSocket.onmessage = { (e: dom.MessageEvent) =>
      dom.document.getElementById("websocketMessages").appendChild(li(e.data.toString).render)
    }
    webSocket
  }

  def myContent = div(
    h1(id := "title", "This is a title"),
    p("This is a proof that we can do awesome javascripting from client!! haha finally!")
  ).render
}
