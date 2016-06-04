package shared.simulation

import shared.simulation.parameters.CrossingStrategyEnum.CrossingStrategyEnum
import shared.simulation.parameters.MapFileEnum.MapFileEnum

package object parameters {

  object MapFileEnum extends Enumeration {
    type MapFileEnum = Value
    val MAP_1, MAP_2, TEST_MAP = Value

    def toResourceFile(mapFileEnum: MapFileEnum): String = s"server/conf/resources/$mapFileEnum.json"
  }

  object CrossingStrategyEnum extends Enumeration {
    type CrossingStrategyEnum = Value
    val FIRST_IN_FIRST_OUT, RANDOM_TIME_LIGHTS, CONSTANT_TIME_LIGHTS, LOAD_BALANCED_LIGHTS = Value
  }

  case class SimulationParameters(carsMaxNumber: Int, mapFile: MapFileEnum, crossingStrategy: CrossingStrategyEnum)

  object SimulationParameters {
    def default = new SimulationParameters(200, MapFileEnum.MAP_1, CrossingStrategyEnum.FIRST_IN_FIRST_OUT)
  }

  trait CustomEnumerationSerialization {

    import upickle.Js

    implicit val crossingStrategyWriter = upickle.default.Writer[CrossingStrategyEnum] { case e => Js.Str(e.toString) }
    implicit val crossingStrategyReader = upickle.default.Reader[CrossingStrategyEnum] { case Js.Str(e) => CrossingStrategyEnum.withName(e) }

    implicit val mapFileWriter = upickle.default.Writer[MapFileEnum] { case e => Js.Str(e.toString) }
    implicit val mapFileReader = upickle.default.Reader[MapFileEnum] { case Js.Str(e) => MapFileEnum.withName(e) }
  }

}
