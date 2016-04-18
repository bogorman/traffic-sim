package controllers

import play.api.mvc._
import shared.SharedMessages
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def socket = WebSocket.using[String] { request =>

    // Log events to the console
    val in = Iteratee.foreach[String](println).map { _ =>
      println("Disconnected")
    }

    // Send a single 'Hello!' message
    val out = Enumerator("Hello hello from awesome websocket!")

    (in, out)
  }

}
