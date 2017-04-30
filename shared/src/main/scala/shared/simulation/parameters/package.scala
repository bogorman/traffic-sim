package shared.simulation

package object parameters {

  sealed case class MapFileEnum(value: String)
  object MapFileEnum {
    object MAP_1 extends MapFileEnum("MAP_1")
    object MAP_2 extends MapFileEnum("MAP_2")
    object TEST_MAP extends MapFileEnum("TEST_MAP")

    val values = Seq(MAP_1, MAP_2, TEST_MAP)
    def stringValues = values.map(_.value).toList

    def toResourceFile(mapFileEnum: MapFileEnum): String = s"server/conf/resources/${mapFileEnum.value}.json"    

    def withName(value: String) = { MapFileEnum(value)  }
  }  


  sealed case class CrossingStrategyEnum(value: String)
  object CrossingStrategyEnum {
    object FIRST_IN_FIRST_OUT extends CrossingStrategyEnum("FIRST_IN_FIRST_OUT")
    object RANDOM_TIME_LIGHTS extends CrossingStrategyEnum("RANDOM_TIME_LIGHTS")
    object CONSTANT_TIME_LIGHTS extends CrossingStrategyEnum("CONSTANT_TIME_LIGHTS")
    object LOAD_BALANCED_LIGHTS extends CrossingStrategyEnum("LOAD_BALANCED_LIGHTS")    

    val values = Seq(FIRST_IN_FIRST_OUT, RANDOM_TIME_LIGHTS, CONSTANT_TIME_LIGHTS, LOAD_BALANCED_LIGHTS)
    def stringValues = values.map(_.value).toList

    def withName(value: String) = { CrossingStrategyEnum(value) }
  }    

  case class SimulationParameters(carsMaxNumber: Int,mapFile: MapFileEnum, crossingStrategy: CrossingStrategyEnum)

  val defaultSimulationParameters = SimulationParameters(200, MapFileEnum.MAP_1, CrossingStrategyEnum.FIRST_IN_FIRST_OUT)

}
