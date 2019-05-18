import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scalacamp",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += cats,
    libraryDependencies += akkaActor,
    libraryDependencies += akkaSteam,
    libraryDependencies += akkaHttp,
    libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
    libraryDependencies += slick,
    libraryDependencies += h2
  )
