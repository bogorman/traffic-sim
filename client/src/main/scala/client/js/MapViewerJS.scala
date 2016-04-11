package client.js

import scala.scalajs.js
import org.scalajs.dom

object MapViewerJS extends js.JSApp {
  def main(): Unit = {
    dom.document.getElementById("visualizationHeader").textContent = "Visualization"

    val canvas = dom.document.getElementById("mapCanvas").asInstanceOf[dom.html.Canvas]
    val context = canvas.getContext("2d")
    context.strokeRect(5,5,25,15)
    context.scale(2,2)
    context.strokeRect(5,5,25,15)
  }
}

