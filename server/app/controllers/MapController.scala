package controllers

import akka.pattern.Patterns.ask
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.mvc._
import shared.map.{MapApi, RoadMap}
import system.{ActorManager, ApiImplementation, MyServer}
import system.MapAgent._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import autowire._
import upickle._


object MapController extends Controller {

  def timeout: FiniteDuration = 3 seconds

  def showAll = Action.async { request =>
    val futureResult = ask(ActorManager.mapAgent, GetMap, timeout * 2).collect {
      case map: RoadMap => Ok(views.html.allRoads(map))
    }

    val timeoutResult = Promise.timeout(RequestTimeout("timeout"), timeout)
    Future.firstCompletedOf(Seq(futureResult, timeoutResult))
  }

  def shortestRoute(start: String, end: String) = Action.async { request =>
    val futureResult = ask(ActorManager.mapAgent, ShortestRouteRequest(start, end), timeout * 2).collect {
      case UnknownNodes => NotFound("Unknown crossings")
      case Unreachable => NotFound("Unreachable")
      case Route(roads) => Ok(views.html.path(start, end, roads))
    }

    val timeoutResult = Promise.timeout(RequestTimeout("timeout"), timeout)
    Future.firstCompletedOf(Seq(futureResult, timeoutResult))
  }

  def test(apiMethod: String) = Action.async { request =>
    MyServer.route[MapApi](ApiImplementation)(autowire.Core.Request(
      apiMethod.split("/"),
      upickle.json.read(request.body.asText.getOrElse("{}")).asInstanceOf[Js.Obj].value.toMap))
      .map(upickle.json.write(_))
      .map(Ok(_))
//    Ok(apiMethod)
  }
}
