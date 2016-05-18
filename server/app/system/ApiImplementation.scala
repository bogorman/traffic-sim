package system

import java.io.FileInputStream

import akka.pattern.ask
import akka.util.Timeout
import autowire.Server
import com.google.inject.Inject
import play.api.libs.json.Json
import shared.MapApi
import shared.geometry._
import shared.map.{CrossingDef, RoadDef, RoadMap}
import system.MapAgent.GetMap
import upickle.Js
import upickle.default._
import utils.map.json._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class ApiImplementation @Inject()(actorManager: ActorManager) extends MapApi {
  override def test: String = "Takie Ajaxy ktore dzialaja wysmienicie"

  override def map(): RoadMap = {
    val duration = 2 seconds
    implicit val timeout: Timeout = Timeout(duration)
    Await.result(actorManager.mapAgent.ask(GetMap), duration).asInstanceOf[RoadMap]
  }

  def mockMap1(): RoadMap = {
    val n1 = "raz"
    val n2 = "dwa"
    val cross1 = CrossingDef(n1, 50.0 >< 70.0)
    val cross2 = CrossingDef(n2, 100.0 >< 120.0)
    RoadMap(List(cross1, cross2), List(RoadDef(n1, n2, List.empty)))
  }

  def mockMap2(): RoadMap = Json.parse(new FileInputStream("map.json")).as[RoadMap]
}

object MyServer extends Server[Js.Value, Reader, Writer] {

  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)


  //  val routes = MyServer.route[MapApi](ApiImplementation)
}