import Dependencies._

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "green.thisfieldwas"
ThisBuild / organizationName := "example"

addCompilerPlugin(
  ("org.typelevel" % "kind-projector" % "0.13.2").cross(CrossVersion.full)
)

lazy val root = (project in file("."))
  .settings(
    name := "random",
    scalacOptions ++= Seq("-Xlint", "-Xfatal-warnings"),
    libraryDependencies ++= testDependencies
  )
