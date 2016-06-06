package system.simulation.strategy
import shared.map.Road
import system.simulation.Car

case object ConstantLightsStrategy extends CrossingStrategy {
  override def addCar(car: Car): CrossingStrategy = ???

  override def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy) = ???
}
