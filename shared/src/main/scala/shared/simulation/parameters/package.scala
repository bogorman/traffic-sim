package shared.simulation

package object parameters {

  sealed trait MapFile {
    private val resourcesPathPrefix = "server/conf/resources/"

    final def stringName = s"$resourcesPathPrefix$strName"

    protected def strName: String
  }

  case object Map1 extends MapFile {
    override def strName: String = "map.json"
  }

  case object Map2 extends MapFile {
    override protected def strName: String = "map2.json"
  }

  sealed trait CrossingStrategyDAO

  case object FirstInFirstOutDAO extends CrossingStrategyDAO

  // todo moar strategies we neeed

  case class SimulationParameters(carsMaxNumber: Int, mapFile: MapFile, crossingStrategy: CrossingStrategyDAO)

  object SimulationParameters {
    def default = new SimulationParameters(200, Map1, FirstInFirstOutDAO)
  }
}
