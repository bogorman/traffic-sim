package shared

import scala.concurrent.duration._
import scala.language.postfixOps

object Constants {
  val statisticsInterval: Int = 20

  val crossingDiameter = 4.0
  val safeDistance = 20.0
  val speed = 3.0 // odleglosc, ktora sie przesunie w kazdym ticku; todo: zrobic kontrolke do tego

  val simulationStepDuration: FiniteDuration = 30 milliseconds
}
