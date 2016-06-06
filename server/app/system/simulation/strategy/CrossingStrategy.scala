package system.simulation.strategy

import akka.actor.ActorRef
import shared.map.Road
import shared.simulation.parameters.CrossingStrategyEnum._
import system.simulation.Car

trait CrossingStrategy {
  def addCar(car: Car): CrossingStrategy

  def nextCar(blockedRoads: Set[Road]): (Option[Car], CrossingStrategy)
}

object CrossingStrategy {
  def createFromDao(crossingStrategyEnum: CrossingStrategyEnum, inRoads: List[ActorRef]) = crossingStrategyEnum match {
    case FIRST_IN_FIRST_OUT => FirstInFirstOutStrategy()
    case CONSTANT_TIME_LIGHTS => ConstantLightsStrategy(inRoads)
    case _ => FirstInFirstOutStrategy() // todo: implement other strategies
  }
}