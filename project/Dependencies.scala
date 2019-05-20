import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.7"
  lazy val cats = "org.typelevel" %% "cats" % "0.9.0"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.6.0-M1"
  lazy val akkaSteam = "com.typesafe.akka" %% "akka-stream" % "2.6.0-M1"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.8"
  lazy val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8"
  lazy val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.0-M1"
  lazy val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % "10.1.8"
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.3.0"
  lazy val h2 = "com.h2database" % "h2" % "1.4.199"
}
