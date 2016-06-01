package system.simulation.strategy

import shared.map.Road
import system.simulation.Car

import scala.collection.immutable.Queue

case class FirstInFirstOutStrategy(waitingCars: Queue[Car]) extends CrossingStrategy {
  override def addCar(car: Car): CrossingStrategy =
    copy(waitingCars = waitingCars enqueue car)

  override def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy) = {
    if (waitingCars.nonEmpty) {
      waitingCars.dequeue match {
        case (car@Car(_, _, _, _, nextRoad :: _), newQueue) if blockedRoads contains nextRoad =>
          (None, copy(waitingCars = newQueue enqueue car))
        case (car, newQueue) =>
          (Some(car), copy(waitingCars = newQueue))
      }
    } else {
      (None, this)
    }
  }
}

object FirstInFirstOutStrategy {
  def apply(): FirstInFirstOutStrategy = new FirstInFirstOutStrategy(Queue.empty)
}
