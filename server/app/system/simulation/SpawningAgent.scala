package system.simulation

import java.util.UUID

import akka.actor.ActorRef
import shared.Constants
import shared.map.{Crossing, RoadMap}
import system.MapAgent
import system.MapAgent.Route
import system.simulation.CrossingAgent.SpawnCar
import system.simulation.SpawningAgent.{SpawningInit, SpawningState}

import scala.util.Random

object SpawningAgent {
  // supported changes
  case class CrossingFreed(tick: Long, crossing: Crossing) extends TickMsg

  // init message
  case class SpawningInit(sources: Map[Crossing, ActorRef], sinks: Map[Crossing, ActorRef]) extends SimulationAgent.AgentInit {
    override def neighbours: List[ActorRef] = sources.values.toList ++ sinks.values
  }

  // state
  case class SpawningState(map: RoadMap, sources: Map[Crossing, ActorRef], sinks: Map[Crossing, ActorRef], freeSources: Set[Crossing], carCount: Int)
    extends SimulationAgent.AgentState[SpawningState] {

    override def update(changes: List[TickMsg]): SpawningState = changes.foldLeft(this) {
      case (self, CrossingFreed(_, crossing)) if sources contains crossing => self.copy(freeSources = freeSources + crossing)
      case (self, CrossingFreed(_, crossing)) if sinks contains crossing => self.copy(carCount = carCount - 1)
    }

    override def nextStep: (SpawningState, Map[ActorRef, (Long) => TickMsg]) = {
      println("(carCount, Constants.carsMaxNumber) = " +(carCount, Constants.carsMaxNumber))
      if (carCount < Constants.carsMaxNumber && freeSources.nonEmpty) {
        val source: Crossing = randomFromIterable(freeSources)
        val destination: Crossing = randomFromIterable(sinks.keySet)
        println("(source, destination) = " +(source, destination))
        MapAgent.dijkstra(map, source, destination) match {
          case Route(roads) =>
            (copy(freeSources = freeSources - source, carCount = carCount + 1), msgMap +
              (sources(source) -> {SpawnCar(_, Car(UUID.randomUUID().toString, source.coordinates.x, source.coordinates.y, sources(source), roads.toList))}))
          case _ =>
            (this, msgMap)
        }
      } else
        (this, msgMap)
    }

    private def randomFromIterable[A](iterable: Iterable[A]): A = iterable.iterator.drop(Random.nextInt(iterable.size)).next()
  }

}

class SpawningAgent(map: RoadMap) extends SimulationAgent[SpawningState, SpawningInit](map.sources.length + map.sinks.length) {
  override def clearState(init: SpawningInit): SpawningState = SpawningState(map, init.sources, init.sinks, init.sources.keySet, 0)
}
