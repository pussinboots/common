name := "common"

version in ThisBuild := "0.1"

organization := "org.frank"

scalaVersion in ThisBuild := "2.9.1"

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

// test dependencies
libraryDependencies ++= Seq(
    "co.freeside" % "betamax" % "1.1.2",
    "org.codehaus.groovy" % "groovy-all" % "1.8.8",
    "org.scalatest" %% "scalatest" % "1.6.1"
)
