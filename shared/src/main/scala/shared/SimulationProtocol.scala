package shared

import jsactor.bridge.protocol.UPickleBridgeProtocol
import jsactor.bridge.protocol.UPickleBridgeProtocol.MessageRegistry

import scala.reflect.ClassTag

object SimulationProtocol extends UPickleBridgeProtocol {
    import map._
    import simulation.parameters._

  override def registerMessages(registry: MessageRegistry): Unit = {
    registry.add[CarsUpdate]
    registry.add[RoadMap]
    registry.add[SimulationParameters]
  }

}
