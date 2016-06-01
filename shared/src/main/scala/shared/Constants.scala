package shared

import scala.concurrent.duration._
import scala.language.postfixOps


object Constants {
  val crossingDiameter = 4.0
  val safeDistance = 11.0
  val speed = 3.0 // odleglosc, ktora sie przesunie w kazdym ticku; todo: zrobic kontrolke do tego

  val carsMaxNumber = 201

  val simulationStepDuration: FiniteDuration = 30 milliseconds
}
