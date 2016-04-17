package system.simulation

import akka.actor.{Actor, ActorRef}
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString}
import shared.map.{Crossing, RoadMap}

object SimulationManager {

  case class UpdateQueueCreated(actorRef: ActorRef)

  case class CarsMoved(tick: Long, cars: Seq[Car]) extends EndMessage {
    override def json: JsArray = JsArray(cars map {
      case Car(id, x, y, _, _) => JsObject(Seq(
        "moved" -> JsString(id), "x" -> JsNumber(x), "y" -> JsNumber(y)))
    })
  }

  case class CarRemoved(tick: Long, car: Car) extends EndMessage {
    override def json: JsArray = JsArray(Seq(JsObject(Seq("removed" -> JsString(car.id)))))
  }

  case class CarSpawned(tick: Long, car: Car, target: Crossing) extends EndMessage {
    override def json: JsArray = JsArray(Seq(JsObject(Seq(
      "spawned" -> JsString(car.id), "x" -> JsNumber(car.x), "y" -> JsNumber(car.y), "to" -> JsString(target.name)))))
  }
}

class SimulationManager(map: RoadMap, outputStream: ActorRef) extends Actor {

  locally {

  }

  override def receive: Receive = ???
}
