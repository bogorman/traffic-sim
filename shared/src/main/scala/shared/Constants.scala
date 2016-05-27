package shared

import scala.concurrent.duration._
import scala.language.postfixOps


object Constants {
  val crossingDiameter = 3.0
  val safeDistance = 6.0
  val speed = 3.0

  val carsMaxNumber = 80

  val simulationStep: FiniteDuration = 30 milliseconds
}
