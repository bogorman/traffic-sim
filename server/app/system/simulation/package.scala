package system

import play.api.libs.json.JsArray

package object simulation {
  trait TickMsg {
    def tick: Long
  }

  trait StateChangedMessage extends TickMsg

  case class NoOp(tick: Long) extends StateChangedMessage


  case object Start

  case object Ack
}

