package system.simulation.strategy

import shared.map.Road
import shared.simulation.parameters.CrossingStrategyEnum._
import system.simulation.Car

trait CrossingStrategy {
  def addCar(car: Car): CrossingStrategy

  def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy)
}

object CrossingStrategy {
  def createFromDao(crossingStrategyEnum: CrossingStrategyEnum) = crossingStrategyEnum match {
    case FIRST_IN_FIRST_OUT => FirstInFirstOutStrategy()
    case _ => FirstInFirstOutStrategy() // todo: implement other strategies
  }
}