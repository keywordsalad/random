import Dependencies._

scalaVersion := "2.12.10"
crossScalaVersions := Seq("2.12.10", "2.12.17", "2.13.10")

version := "0.1.0-SNAPSHOT"
organization := "green.thisfieldwas"
organizationName := "This Field Was Green"
organizationHomepage := Some(url("https://thisfieldwas.green"))
description := "A useful library of immutable random number generators"
licenses := Seq("APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

lazy val root = (project in file("."))
  .settings(
    name := "random",
    scalacOptions ++= Seq("-Xlint", "-Xfatal-warnings"),
    libraryDependencies ++= testDependencies
  )

import xerial.sbt.Sonatype.ProjectHosting
sonatypeProjectHosting := Some(
  ProjectHosting(
    domain = "thisfieldwas.green",
    user = "keywordsalad",
    fullName = Some("Logan McGrath"),
    email = "logan.mcgrath@thisfieldwas.green",
    repository = "https://bitsof.thisfieldwas.green/keywordsalad/random"
  )
)
