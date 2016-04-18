package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee._
import play.api.mvc._
import shared.SharedMessages

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def socket = WebSocket.using[String] { request =>
    val out = Enumerator.repeatM(Promise.timeout("Hello!", 3000))
    val in = Iteratee.ignore[String]

    (in, out)
  }
}
