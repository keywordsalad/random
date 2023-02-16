import Dependencies._

scalaVersion := "2.12.10"
crossScalaVersions := Seq("2.12.10", "2.13.10")

version := "0.1.0-SNAPSHOT"
organization := "green.thisfieldwas.random"
organizationName := "random"

addCompilerPlugin(
  ("org.typelevel" % "kind-projector" % "0.13.2").cross(CrossVersion.full)
)

lazy val root = (project in file("."))
  .settings(
    name := "random",
    scalacOptions ++= Seq("-Xlint", "-Xfatal-warnings"),
    libraryDependencies ++= testDependencies
  )
