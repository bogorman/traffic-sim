import sbt.Project.projectToRef

lazy val clients = Seq(client)
lazy val scalaV = "2.11.8"
lazy val scalacOpt = Seq("-feature")

// lazy val upickleV = "0.2.8"
// lazy val upickleV = "0.4.0"
// lazy val upickleV = "0.4.0"
lazy val upickleV = "0.3.7"
// lazy val jsactorV = "0.6.1"
lazy val jsactorV = "0.6.8"

lazy val server = (project in file("server")).settings(
  scalacOptions ++= scalacOpt,
  routesGenerator := InjectedRoutesGenerator,
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers ++= Seq(
    "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"),
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.4.0",
    "org.webjars" % "jquery" % "1.11.1",
    "com.typesafe.play" %% "play-json" % "2.4.3",
    "com.typesafe.akka" %% "akka-actor" % "2.4.5",
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.5",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.1.0",
    "de.ummels" %%% "scala-prioritymap" % "0.5.0",
    "com.lihaoyi" %% "upickle" % upickleV,
    "com.codemettle.jsactor" %%% "jsactor-bridge-server" % jsactorV,
    "com.codemettle.jsactor" %%% "jsactor-bridge-server-upickle" % jsactorV,
    specs2 % Test
  )
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalacOptions ++= scalacOpt,
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "com.lihaoyi" %%% "scalatags" % "0.5.4",
    "com.lihaoyi" %%% "upickle" % upickleV,
    "com.github.karasiq" %%% "scalajs-bootstrap" % "1.0.5",
    "com.codemettle.jsactor" %%% "jsactor-bridge-client" % jsactorV,
    "com.codemettle.jsactor" %%% "jsactor-bridge-client-upickle" % jsactorV    
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalacOptions ++= scalacOpt,
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % upickleV,
      "com.codemettle.jsactor" %%% "jsactor-bridge-shared" % jsactorV,
      "com.codemettle.jsactor" %%% "jsactor-bridge-shared-upickle" % jsactorV
    )
  ).jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value