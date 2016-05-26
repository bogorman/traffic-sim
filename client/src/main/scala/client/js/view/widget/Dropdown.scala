package client.js.view.widget

import com.karasiq.bootstrap.BootstrapAttrs._
import io.udash.wrappers.jquery._
import org.scalajs.dom.html.{Button, UList}
import org.scalajs.dom.raw.Node

import scala.util.Random
import scalatags.JsDom.all._

class Dropdown(categories: List[String]) extends Widget {

  val dropdownMenuID = "dropdownMenu" + Random.nextInt()

  val toggleListVisibility = () => jQ(comboList).toggleClass("displayStyle")

  val comboButton: Button = button(
    `class` := "btn btn-default dropdown-toggle",
    `type` := "button",
    id := dropdownMenuID,
    `data-toggle` := "dropdown",
    aria.haspopup := "true",
    aria.expanded := "true",
    onclick := toggleListVisibility,
    createDropdownContent(categories(0))).render

  val comboList: UList = ul(
    `class` := "dropdown-menu",
    aria.labelledby := dropdownMenuID,
    categories.map(value => li(
      value,
      onclick := { () =>
        toggleListVisibility()
        comboButton.removeChild(comboButton.firstChild)
        comboButton.appendChild(createDropdownContent(value))
      }))).render

  def createDropdownContent(value: String): Node = {
    div(
      span()(value + " "),
      span(`class` := "caret")).render
  }

  override def root(): Node = div(
    `class` := "dropdown",
    comboButton,
    comboList
  ).render
}
