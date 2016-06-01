package system.simulation

import akka.actor.Actor

import utils.MapUtils._

class UpdateQueue(neighboursNumber: Int, name: String) extends Actor {

  override def receive: Receive = collectUpdates(Map.empty withDefaultValue neighboursNumber, Map.empty withDefaultValue List.empty, started = false)

  private def collectUpdates(ticks: Map[Long, Int], changes: Map[Long, List[TickMsg]], started: Boolean): Receive = {
    case NoOp(current) => adjustTicks(current, ticks, changes, started)
    case m: TickMsg => adjustTicks(m.tick, ticks, changes.adjust(m.tick) {m :: _}, started)
    case Start =>
      if (ticks(0) == 0) {
        context.parent ! TickMsgs(changes(0))
        context become collectUpdates(ticks - 0, changes - 0, started = true)
      } else {
        context become collectUpdates(ticks, changes, started = true)
      }
  }

  private def adjustTicks(current: Long, ticks: Map[Long, Int], changes: Map[Long, List[TickMsg]], started: Boolean): Unit = {
    val (newTicks, newValue) = ticks.adjustWithValue(current) {_ - 1}
    if (newValue == 0 && started) {
      context.parent ! TickMsgs(changes(current))
      context become collectUpdates(newTicks - current, changes - current, started)
    } else {
      context become collectUpdates(newTicks, changes, started)
    }
  }
}
