package system.simulation

import akka.actor.{Actor, ActorRef, Cancellable, Props}
import shared.Constants
import system.simulation.CrossingAgent.EnterCrossing
import system.simulation.SimulationAgent._
import system.simulation.SimulationManager.UpdateQueueCreated

import scala.concurrent.duration.Duration
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

  case object DoStep
}

abstract class SimulationAgent[State <: AgentState[State], Init <: AgentInit : ClassTag](neighboursNumber: Int) extends Actor {

  var scheduledUpdate: Option[Cancellable] = None

  def printState(a: Any): Unit = println(s"${getClass.getName} :: $a")

  private val updateQueue: ActorRef = context.actorOf(Props(classOf[UpdateQueue], neighboursNumber, s"q${self.path.name}"), s"q${self.path.name}")
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
      scheduledUpdate = Option(context.system.scheduler.schedule(Duration.Zero, Constants.simulationStep, self, DoStep)(context.system.dispatcher))
      context become working(1, newState, neighbours, canProceed = false, None)
  }

  private def working(tick: Long, state: State, neighbours: List[ActorRef], canProceed: Boolean, receivedChanges: Option[List[TickMsg]]): Receive = {
    case TickMsgs(changes) if canProceed =>
      applyChanges(tick, state, neighbours, changes)

    case TickMsgs(changes) =>
      context become working(tick, state, neighbours, canProceed, Option(changes))

    case DoStep if receivedChanges.isDefined =>
      applyChanges(tick, state, neighbours, receivedChanges.get)

    case DoStep =>
      context become working(tick, state, neighbours, canProceed = true, receivedChanges)
  }

  private def applyChanges(tick: Long, state: State, neighbours: List[ActorRef], changes: List[TickMsg]): Unit = {
    val (newState, msgs) = state.update(changes).nextStep
    context.parent :: neighbours foreach { n =>
//      if (!msgs(n)(tick).isInstanceOf[NoOp]) {
//        println(s"$tick: ${self.path.name} -> ${n.path.name} (${msgs(n)(tick).getClass.getSimpleName})")
//      }
      msgs(n)(tick) match {
        case EnterCrossing(_, car) => println(s" + $tick: ${self.path.name} -> ${n.path.name} ($car)")
        case _ =>
      }
      n ! msgs(n)(tick)
    }
    context become working(tick + 1, newState, neighbours, canProceed = false, None)
  }

  protected def clearState(init: Init): State

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    scheduledUpdate foreach { _.cancel() }
    println(s"!!! stopping ${this.getClass.getSimpleName}")
  }
}
