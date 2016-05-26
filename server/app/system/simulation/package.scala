package system

package object simulation {
  trait TickMsg {
    def tick: Long
  }

  case class TickMsgs(msgs: List[TickMsg])

  trait StateChangedMessage extends TickMsg

  case class NoOp(tick: Long) extends StateChangedMessage


  case object Start

  case object Ack
}

