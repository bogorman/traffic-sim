package client.js.view.widget

import io.udash.wrappers.jquery._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.Node

import scalatags.JsDom.all._

class Spinner(initialValue: Int) extends Widget {

  val inputBox = input(
    `class` := "form-control",
    value := initialValue).render

  def createArrowButton(arrowUp: Boolean, valueMapper: (Int) => Int): Node = {
    div(`class` := "input-group-addon fixed-btn",
      button(
        `class` := "btn btn-default",
        onclick := { () =>
          jQ(inputBox).value((_: Element, _: Int, value: String) => valueMapper(Integer.parseInt(value)).toString)
        },
        span(`class` := {
          if (arrowUp) "caret caret-reversed" else "caret"
        }))).render
  }

  override def root(): Node = div(
    `class` := "input-group",
    createArrowButton(false, value => Math.max(0, value - 1)),
    inputBox,
    createArrowButton(true, value => value + 1)).render
}
