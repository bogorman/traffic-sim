package shared.simulation

package object parameters {

  trait Enum[A] {
    trait Value { self: A =>
      _values :+= this
    }
    private var _values = List.empty[A]
    def values = _values

    def stringValues = values.map(_.toString)

  }

  sealed trait MapFileEnum extends MapFileEnum.Value
  object MapFileEnum extends Enum[MapFileEnum] {
    case object MAP_1 extends MapFileEnum; MAP_1
    case object MAP_2 extends MapFileEnum; MAP_2
    case object TEST_MAP extends MapFileEnum; TEST_MAP

    val elements = Set(MAP_1,MAP_2,TEST_MAP)
    val elementStrings = elements.map{e => e.toString}

    def withName(name: String): MapFileEnum = {
      elements.filter{e => e.toString == name}.head
    }    

    def toResourceFile(mapFileEnum: MapFileEnum): String = s"server/conf/resources/$mapFileEnum.json"
  }    

  sealed trait CrossingStrategyEnum extends CrossingStrategyEnum.Value
  object CrossingStrategyEnum extends Enum[CrossingStrategyEnum] {
    case object FIRST_IN_FIRST_OUT extends CrossingStrategyEnum; FIRST_IN_FIRST_OUT
    case object RANDOM_TIME_LIGHTS extends CrossingStrategyEnum; RANDOM_TIME_LIGHTS
    case object CONSTANT_TIME_LIGHTS extends CrossingStrategyEnum; CONSTANT_TIME_LIGHTS
    case object LOAD_BALANCED_LIGHTS extends CrossingStrategyEnum; LOAD_BALANCED_LIGHTS

    val elements = Set(FIRST_IN_FIRST_OUT,RANDOM_TIME_LIGHTS,CONSTANT_TIME_LIGHTS,LOAD_BALANCED_LIGHTS)
    val elementStrings = elements.map{e => e.toString}

    def withName(name: String):CrossingStrategyEnum = {
      elements.filter{e => e.toString == name}.head
    }    
  }    

  case class SimulationParameters(carsMaxNumber: Int,mapFile: MapFileEnum, crossingStrategy: CrossingStrategyEnum)

  val defaultSimulationParameters = SimulationParameters(200, MapFileEnum.MAP_1, CrossingStrategyEnum.FIRST_IN_FIRST_OUT)

}
