package system.simulation

import akka.actor.ActorRef
import shared.Constants
import shared.geometry.{Coordinates, distance, segmentOffset}
import shared.map.Road
import system.simulation.CrossingAgent.{EnterCrossing, UnblockRoad}
import system.simulation.RoadAgent._
import system.simulation.SimulationManager.CarsMoved

import scala.language.postfixOps

object RoadAgent {

  // supported changes
  case class LeaveCrossing(tick: Long, car: Car) extends TickMsg

  case class CarTaken(tick: Long) extends TickMsg

  // init message
  case class RoadInit(start: ActorRef, end: ActorRef) extends SimulationAgent.AgentInit {
    override def neighbours: List[ActorRef] = start :: end :: Nil
  }

  // state
  case class RoadState(road: Road, cars: List[(Car, Double)], controller: ActorRef, start: ActorRef, end: ActorRef,
                       wasTaken: Boolean) extends SimulationAgent.AgentState[RoadState] {

    private val length = road.length

    override def update(changes: List[TickMsg]): RoadState = {
      changes.foldLeft(this) {
        case (self, LeaveCrossing(_, car)) =>
          self.copy(cars = self.cars :+ (car, Constants.crossingDiameter))
        case (self, CarTaken(_)) => self.copy(cars = self.cars.tail, wasTaken = true)
      }
    }

    override def nextStep: (RoadState, Map[ActorRef, (Long) => TickMsg]) = cars match {
      case List.empty =>
        (copy(wasTaken = false), msgMap)

      case first :: _ =>
        val newFirst: Option[(Car, Double)] =
          if (first._2 < length - Constants.crossingDiameter) {
            val newOffset = Math.min(first._2 + Constants.speed, length - Constants.crossingDiameter + 1)
            val newCoordinates = offset(newOffset)
            Some((first._1.copy(x = newCoordinates.x, y = newCoordinates.y), newOffset))
          } else None

        val newCarsTail: Iterator[(Car, Double)] = cars sliding 2 filter {
          _.size == 2
        } map {
          case List((prev, prevOff), (curr, currOff)) =>
            if (prevOff - currOff > Constants.safeDistance) {
              val newOffset = currOff + Constants.speed
              val newCoordinates = offset(newOffset)
              (curr.copy(x = newCoordinates.x, y = newCoordinates.y), newOffset)
            } else (curr, currOff)
        }

        val newCars: List[(Car, Double)] = newFirst.getOrElse(cars.head) :: newCarsTail.toList

        val movedCars: List[Car] = cars zip newCars collect {
          case ((_, oldOff), (newCar, newOff)) if oldOff != newOff => newCar
        }

        val map1: Map[ActorRef, (Long) => TickMsg] = msgMap + (controller -> {
          CarsMoved(_, movedCars)
        })

        val map2: Map[ActorRef, (Long) => TickMsg] = if ((wasTaken || cars.head._2 != newCars.head._2) && newCars.head._2 >= (length - Constants.crossingDiameter)) {
          map1 + (end -> {
            EnterCrossing(_, newCars.head._1)
          })
        } else {
          map1
        }

        val map3: Map[ActorRef, (Long) => TickMsg] = {
          if (cars.last._2 < Constants.crossingDiameter + Constants.safeDistance && newCars.last._2 >= Constants.crossingDiameter + Constants.safeDistance) {
            map2 + (start -> {
              UnblockRoad(_, road)
            })
          } else {
            map2
          }
        }

        (copy(cars = newCars, wasTaken = false), map3)
    }

    private def offset(value: Double): Coordinates = {
      @scala.annotation.tailrec
      def find(value: Double, segments: List[List[Coordinates]]): Coordinates = segments match {
        case (List(a, b) :: rest) =>
          val dist = distance(a, b)
          if (dist < value) find(value - dist, rest)
          else segmentOffset(a, b, value)
      }

      find(value, (road.start.coordinates :: road.bendingPoints ::: road.end.coordinates :: Nil) sliding 2 toList)
    }


  }
}

class RoadAgent(road: Road) extends SimulationAgent[RoadState, RoadInit](2) {
  override protected def clearState(init: RoadInit): RoadState = RoadState(road, Nil, context.parent, init.start, init.end, wasTaken = false)
}
