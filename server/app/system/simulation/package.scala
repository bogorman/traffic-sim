package system

import play.api.libs.json.JsArray

package object simulation {
  trait TickMsg {
    def tick: Long
  }

  trait EndMessage extends TickMsg {
    def json: JsArray
  }

  case class NoOp(tick: Long) extends EndMessage {
    override def json: JsArray = JsArray()
  }


  case object Start

  case object Ack
}

