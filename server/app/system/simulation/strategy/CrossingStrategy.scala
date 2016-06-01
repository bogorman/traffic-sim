package system.simulation.strategy

import shared.map.Road
import system.simulation.Car

trait CrossingStrategy {

  def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy)

  def addCar(car: Car): CrossingStrategy

}