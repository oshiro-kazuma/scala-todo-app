name := """scala-todo-app"""
organization := "info.ooshiro"

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.12.6"

//libraryDependencies += guice
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "info.ooshiro.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "info.ooshiro.binders._"


lazy val `play` = project.
  settings(Settings.commons).
  settings(libraryDependencies ++= Seq(
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  )).
  enablePlugins(PlayScala).
  dependsOn(`domain`)

lazy val `domain` = project.settings(Settings.commons)

lazy val root = project in file(".")


