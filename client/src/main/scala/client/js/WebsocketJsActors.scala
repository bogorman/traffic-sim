package client.js

import jsactor.bridge.client.UPickleSocketManager
import jsactor.bridge.client.util.RemoteActorListener

// import jsactor.logging.impl.JsPrintlnActorLoggerFactory
import jsactor.logging.impl.JsNullActorLoggerFactory

import jsactor.{JsActorRef, JsActorSystem}
import org.scalajs.dom
// import jsactor.bridge.client.{SocketManager, WebSocketActor, WebSocketManager}
import jsactor.bridge.client.{ WebSocketManager, UPickleWebSocketManager , UPickleSocketManager}

import scala.scalajs.js.Dynamic

import shared._

case object ProxyConnected

class ProxyActor(remoteActorPath: String) extends RemoteActorListener {
  override def actorPath: String = remoteActorPath

  override def onConnect(serverActor: JsActorRef): Unit = {
    println("onConnect")
    context.parent ! ProxyConnected
  }

  override def whenConnected(serverActor: JsActorRef): Receive = {
    case msg => {
      if (sender() == context.parent) {
        // println("Sending message to serverActor")
        serverActor ! msg
      } else {
        // println("Sending message to parent")
        context.parent ! msg
      }
    }
  }

  override def webSocketManager: WebSocketManager = {
    // WebsocketJsActors.wsManager
    webSManager
  }

  lazy val webSManager = {
    println("webSocketUrl 1")
    // val webSocketUrl = dom.window.asInstanceOf[Dynamic].webSocketUrl.asInstanceOf[String]
    val webSocketUrl = "ws://localhost:9000/sim"
    println("webSocketUrl 2")
    println("webSocketUrl :" + webSocketUrl)

    implicit val bridgeProtocol = SimulationProtocol
    val config = new UPickleSocketManager.Config(webSocketUrl)
    new UPickleWebSocketManager(config)    
  }
}

// ,WebsocketJsActors.webSocketManager

object WebsocketJsActors {
  val actorSystem = JsActorSystem("SimulationClient", JsNullActorLoggerFactory)

  // val wsManager = {
  //   val webSocketUrl = dom.window.asInstanceOf[Dynamic].webSocketUrl.asInstanceOf[String]

  //   implicit val bridgeProtocol = SimulationProtocol
  //   actorSystem.actorOf(UPickleSocketManager.props(UPickleSocketManager.Config(webSocketUrl)), "socketManager")
  // }

  // val webSocketManager = {
  //   val webSocketUrl = dom.window.asInstanceOf[Dynamic].webSocketUrl.asInstanceOf[String]

  //   implicit val bridgeProtocol = SimulationProtocol
  //   val config = new UPickleSocketManager.Config(webSocketUrl)
  //   new UPickleWebSocketManager(config)
  // } 

}
