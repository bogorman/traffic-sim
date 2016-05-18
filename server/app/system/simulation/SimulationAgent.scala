package system.simulation

import akka.actor.{Actor, ActorRef, Props}
import system.simulation.SimulationAgent._
import system.simulation.SimulationManager.UpdateQueueCreated

import scala.reflect.ClassTag

object SimulationAgent {

  trait AgentState[Impl <: AgentState[_]] {
    def update(changes: List[TickMsg]): Impl

    def nextStep: (Impl, Map[ActorRef, Long => TickMsg])

    protected val msgMap: Map[ActorRef, (Long) => TickMsg] = Map().withDefaultValue(NoOp(_))
  }

  trait AgentInit {
    def neighbours: List[ActorRef]
  }
}

abstract class SimulationAgent[State <: AgentState[State], Init <: AgentInit : ClassTag](neighboursNumber: Int) extends Actor {

  def printState(a: Any): Unit = println(s"${getClass.getName} :: $a")

  private val updateQueue: ActorRef = context actorOf Props(classOf[UpdateQueue], neighboursNumber)
  context.parent ! UpdateQueueCreated(updateQueue)

  override def receive: Receive = waitingForInit

  private def waitingForInit: Receive = {
    case init: Init =>
      context.parent ! Ack
      context become waitingForStart(clearState(init), init.neighbours)
  }

  private def waitingForStart(state: State, neighbours: List[ActorRef]): Receive = {
    case Start =>
      val (newState, msgs) = state.nextStep
      neighbours foreach { n =>
        n ! msgs(n)(0)
      }
      updateQueue ! Start
      context become working(1, newState, neighbours)
  }

  private def working(tick: Long, state: State, neighbours: List[ActorRef]): Receive = {
    case changes: List[TickMsg] =>
      val (newState, msgs) = state.update(changes).nextStep
      context.parent :: neighbours foreach { n =>
        n ! msgs(n)(tick)
      }
      context become working(tick + 1, newState, neighbours)
  }

  protected def clearState(init: Init): State
}
