import Dependencies._

scalaVersion := "2.12.10"
crossScalaVersions := Seq("2.12.10", "2.12.17", "2.13.10")

version := "0.1.0-SNAPSHOT"
organization := "green.thisfieldwas"
organizationName := "This Field Was Green"
organizationHomepage := Some(url("https://thisfieldwas.green"))

lazy val root = (project in file("."))
  .settings(
    name := "random",
    scalacOptions ++= Seq("-Xlint", "-Xfatal-warnings"),
    libraryDependencies ++= testDependencies
  )
