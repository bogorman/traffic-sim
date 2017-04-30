package client.js

import client.js.model.StatisticsList
import org.scalajs.dom
import org.scalajs.dom.{Event, MessageEvent}
import shared.map.{CarsUpdate, RoadMap}
import shared.simulation.parameters._
import upickle.default._

import scala.scalajs.js
import jsactor.{JsActor, JsProps}

// SocketMessage

object ClientApp extends js.JSApp {
  def main(): Unit = {
    val mainView = new MainView()
    dom.document.body.appendChild(mainView.wholePage)

    println("ClientApp " + mainView.statisticsChartContext.toString)

    val simActorRef = WebsocketJsActors.actorSystem.actorOf(JsProps(new SimClientActor(mainView)))
  }

}
