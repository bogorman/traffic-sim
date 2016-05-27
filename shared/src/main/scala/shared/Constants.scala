package shared

import scala.concurrent.duration._
import scala.language.postfixOps


object Constants {
  val crossingDiameter = 6.0
  val safeDistance = 11.0
  val speed = 3.0

  val carsMaxNumber = 100

  val simulationStep: FiniteDuration = 30 milliseconds
}
