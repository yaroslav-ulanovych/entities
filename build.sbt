organization := "com.mahpella"

name := "entities"

version := "0.0.3-SNAPSHOT"

scalaVersion := "2.11.2"



sourcesInBase := false

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"



libraryDependencies +=  "org.scala-lang" % "scala-reflect" % "2.11.2"



libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"

libraryDependencies += "org.json4s" %% "json4s-core" % "3.2.10" % "test"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.10" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.2.1" % "test"

libraryDependencies += "com.h2database" % "h2" % "1.4.181" % "test"
