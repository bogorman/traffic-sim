package system.simulation.strategy

import shared.map.Road
import shared.simulation.parameters.{CrossingStrategyDAO, FirstInFirstOutDAO}
import system.simulation.Car

trait CrossingStrategy {
  def addCar(car: Car): CrossingStrategy

  def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy)
}

object CrossingStrategy {
  def createFromDao(crossingStrategyDAO: CrossingStrategyDAO) = crossingStrategyDAO match {
    case FirstInFirstOutDAO => FirstInFirstOutStrategy()
  }
}