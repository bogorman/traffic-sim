package shared

import scala.concurrent.duration._
import scala.language.postfixOps


object Constants {
  val statisticsInterval: Int = 10

  val crossingDiameter = 4.0
  val safeDistance = 11.0
  val speed = 3.0

  val carsMaxNumber = 201

  val simulationStep: FiniteDuration = 30 milliseconds
}
