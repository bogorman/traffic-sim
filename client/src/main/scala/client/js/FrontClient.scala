package client.js

import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._
import autowire.Client
import upickle.{Js, ReaderPicker, WriterPicker}

@JSExport
object FrontClient extends Client[Js.Value, ReaderPicker, WriterPicker] {

  def myContent = div(
    h1(id := "title", "This is a title"),
    p("This is a proof that we can do awesome javascripting from client!! haha finally!")
  ).render

  @JSExport
  def main(): Unit = {

    dom.document.getElementById("scalaMagicClientCode").textContent = "Victory!"
    dom.document.getElementById("scalaMagicClientCode").appendChild(myContent)




  }

  def write[Result: WriterPicker](r: Result) = upickle.default.writeJs(r)
  def read[Result: ReaderPicker](p: Js.Value) = upickle.default.readJs[Result](p)

  override def doCall(req: Request) = {
    println(req)
    
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.json.write(Js.Obj(req.args.toSeq:_*))
    ).map(_.responseText)
      .map(upickle.json.read)

  }
}
