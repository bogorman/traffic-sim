package system.simulation

import java.util.UUID

import akka.actor.ActorRef
import shared.Constants
import shared.map.{Crossing, RoadMap}
import sun.awt.geom.Crossings
import system.MapAgent
import system.MapAgent.Route
import system.simulation.CrossingAgent.SpawnCar
import system.simulation.SpawningAgent.{SpawningInit, SpawningState}

import scala.util.Random

object SpawningAgent {
  // supported changes
  case class CrossingFreed(tick: Long, crossing: Crossing) extends TickMsg

  // init message
  case class SpawningInit(sources: Map[Crossing, ActorRef], sinks: Map[Crossing, ActorRef], crossings: List[ActorRef]) extends SimulationAgent.AgentInit {
    override def neighbours: List[ActorRef] = crossings
  }

  // state
  case class SpawningState(map: RoadMap, sources: Map[Crossing, ActorRef], sinks: Map[Crossing, ActorRef], freeSources: Set[Crossing], carCount: Int, carsMaxNumber: Int)
    extends SimulationAgent.AgentState[SpawningState] {

    override def update(changes: List[TickMsg]): SpawningState = changes.foldLeft(this) {
      case (self, CrossingFreed(_, crossing)) if sources contains crossing => self.copy(freeSources = self.freeSources + crossing)
      case (self, CrossingFreed(_, crossing)) if sinks contains crossing => self.copy(carCount = self.carCount - 1)
      case (self, _) => self
    }

    override def nextStep: (SpawningState, Map[ActorRef, (Long) => TickMsg]) = {
      if (carCount < carsMaxNumber && freeSources.nonEmpty) {
        val source: Crossing = randomFromIterable(freeSources)
        val destination: Crossing = randomFromIterable(sinks.keySet)
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

class SpawningAgent(map: RoadMap, maxCarNumber: Int) extends SimulationAgent[SpawningState, SpawningInit](map.crossings.size) {
  override def clearState(init: SpawningInit): SpawningState = SpawningState(map, init.sources, init.sinks, init.sources.keySet, 0, maxCarNumber)
}
