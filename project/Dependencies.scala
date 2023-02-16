import sbt._

object Dependencies {

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.15.4"
  lazy val scalatestPlusScalaCheck = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0"

  lazy val testDependencies: Seq[ModuleID] = Seq(
    scalaTest,
    scalaCheck,
    scalatestPlusScalaCheck,
  ).map(_ % "test")
}
