val scalazVersion = "7.2.26"

name := """scala-todo-app"""
organization := "info.ooshiro"

version := "1.0-SNAPSHOT"

lazy val `play` = project.
  settings(Settings.commons).
  settings(libraryDependencies ++= Seq(
    guice,
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-iteratee" % scalazVersion,
    "com.pauldijou" %% "jwt-play" % "0.19.0",
    "org.mockito" %% "mockito-scala" % "1.0.0" % Test,
    "org.mockito" % "mockito-core" % "1.8.5" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "com.typesafe.play" %% "play-slick" % "3.0.2",
    "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "mysql" % "mysql-connector-java" % "5.1.44",
  )).
  enablePlugins(PlayScala).
  dependsOn(`domains`)

lazy val `domains` = project.settings(Settings.commons)

lazy val root = project in file(".")


