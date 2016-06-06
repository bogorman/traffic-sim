package system.simulation.strategy
import shared.map.Road
import system.simulation.Car

case class ConstantLightsStrategy(waitingCars: Map[Road, Car]) extends CrossingStrategy {
  override def addCar(car: Car): CrossingStrategy = ???

  override def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy) = ???
}
