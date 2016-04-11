package client.js

import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object FrontClient {

  def myContent = div(
    h1(id := "title", "This is a title"),
    p("This is a proof that we can do awesome javascripting from client!! haha finally!")
  ).render

  @JSExport
  def main(): Unit = {
    dom.document.getElementById("scalaMagicClientCode").textContent = "Victory!"
    dom.document.getElementById("scalaMagicClientCode").appendChild(myContent)
  }
}
