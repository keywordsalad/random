import Dependencies._

val homepageUrl = url("https://bitsof.thisfieldwas.green/keywordsalad/random")

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / crossScalaVersions := Seq("2.12.10", "2.13.10")

ThisBuild / version := "0.1.0"
ThisBuild / organization := "green.thisfieldwas"
ThisBuild / organizationName := "This Field Was Green"
ThisBuild / organizationHomepage := Some(url("https://thisfieldwas.green"))
ThisBuild / description := "A useful library of immutable random number generators"
ThisBuild / homepage := Some(homepageUrl)

ThisBuild / licenses := Seq(
  "APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
)

ThisBuild / scmInfo := Some(
  ScmInfo(
    browseUrl = homepageUrl,
    connection = "scm:git@bitsof.thisfieldwas.green:keywordsalad/random.git"
  )
)

ThisBuild / developers := List(
  Developer(
    "keywordsalad",
    "Logan McGrath",
    "logan.mcgrath@thisfieldwas.green",
    url("https://thisfieldwas.green")
  )
)

ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := sonatypePublishToBundle.value
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

lazy val root = (project in file("."))
  .settings(
    name := "random",
    scalacOptions ++= Seq("-Xlint", "-Xfatal-warnings"),
    libraryDependencies ++= testDependencies
  )

import ReleaseTransformations._

releaseCrossBuild := true
releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
