package client.js

import autowire.Client
import org.scalajs.dom
import upickle.Js
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object FrontClient extends Client[Js.Value, Reader, Writer] {

  def myContent = div(
    h1(id := "title", "This is a title"),
    p("This is a proof that we can do awesome javascripting from client!! haha finally!")
  ).render

  @JSExport
  def main(): Unit = {

    dom.document.getElementById("scalaMagicClientCode").textContent = "Victory!"
    dom.document.getElementById("scalaMagicClientCode").appendChild(myContent)


  }

  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)

  override def doCall(req: Request) = {
    println(req)

    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.json.write(Js.Obj(req.args.toSeq: _*))
    ).map(_.responseText)
      .map(upickle.json.read)

  }
}
