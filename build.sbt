name := """play-scala-seed"""
organization := "com.github.spavikevik"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice
libraryDependencies += "io.7mind.izumi" %% "distage-core" % "1.2.18"
libraryDependencies += "dev.zio" %% "zio" % "2.1.19"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

scalacOptions += "-Xsource:3"

ThisBuild / scalafmtConfig := file(".scalafmt.conf")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.spavikevik.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.spavikevik.binders._"
