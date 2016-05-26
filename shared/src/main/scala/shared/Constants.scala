package shared

import scala.concurrent.duration._
import scala.language.postfixOps


object Constants {
  val crossingDiameter = 0.1
  val safeDistance = 0.2
  val speed = 0.1

  val carsMaxNumber = 8

  val simulationStep: FiniteDuration = 300 milliseconds
}
