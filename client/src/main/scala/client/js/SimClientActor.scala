package client.js

import jsactor.{JsActor, JsProps}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import jsactor.{JsActor, JsProps}
import client.js.model._
import shared.map._
import shared.simulation.parameters._

class SimClientActor(mainView: MainView) extends JsActor {
	println("SimClientActor starting")
  	val proxy = context.actorOf(JsProps(new ProxyActor("/user/SimulationProxy")))
  	println("SimClientActor proxy" + proxy.toString)

    mainView.onFormSubmit(changeSimParams)

	val statisticsViewer = new StatisticsViewer(mainView.statisticsChartContext)
	val statisticsList = new StatisticsList(statisticsViewer.ChartArea)

    println("statisticsViewer :" + statisticsViewer.toString)

	var mapViewer = Option.empty[MapViewer]

  override def preStart(): Unit = {
    // proxy ! Subscribe
    // proxy ! SimulationParameters.default
  	println("SimClientActor preStart")
  }

  override def receive: Receive = {
    // case task: Task =>
    //   tasksMapVar() = tasksMapVar() withTask task

    //   if (sender() != proxy) proxy ! task

    case ProxyConnected => {
    	println("ProxyConnected ok")
    	proxy ! defaultSimulationParameters
    	println("ProxyConnected sent")
    }

    case update: CarsUpdate => {
    	// println("SimClientActor CarsUpdate start")
        update.stats.foreach(statisticsList.addPoint)
        // println("SimClientActor CarsUpdate start 1")
        mapViewer.foreach(_.drawCars(update.cars))
        // println("SimClientActor CarsUpdate start 2")
        statisticsViewer.drawStatistics(statisticsList)
        // println("SimClientActor CarsUpdate done") 
    }

    case mapFromServer: RoadMap => {
    	// println("SimClientActor RoadMap start")
        val newColor = statisticsList.newStatistics()
        mainView.addChartDescription(newColor)        
    	mapViewer = Option(new MapViewer(mainView.simulationMapContext, mapFromServer))
        // println("SimClientActor RoadMap end")
    }

    case _ => println("unknown message")
  }

  def changeSimParams(simulationParameters: SimulationParameters): Unit = {
    // socket.send(write(simulationParameters))
    proxy ! simulationParameters
  }  
}
