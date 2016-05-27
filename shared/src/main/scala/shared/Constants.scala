package shared

import scala.concurrent.duration._
import scala.language.postfixOps


object Constants {
  val crossingDiameter = 3.0
  val safeDistance = 10.0
  val speed = 3.0

  val carsMaxNumber = 300

  val simulationStep: FiniteDuration = 30 milliseconds
}
