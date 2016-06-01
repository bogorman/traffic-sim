package shared

import shared.map.RoadMap
import shared.simulation.parameters.SimulationParameters

trait SimulationApi {
  def initializeSimulation(simulationParameters: SimulationParameters): RoadMap
}