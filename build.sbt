name := """play-medbot-rest"""
organization := "com.piumalkulasekara"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice

// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-bom
libraryDependencies += "com.amazonaws" % "aws-java-sdk-bom" % "1.11.555" pomOnly()

// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.555"

// https://mvnrepository.com/artifact/edu.stanford.swrl/swrlapi
libraryDependencies += "edu.stanford.swrl" % "swrlapi" % "2.0.6"

// https://mvnrepository.com/artifact/edu.stanford.swrl/swrlapi-drools-engine
//libraryDependencies += "edu.stanford.swrl" % "swrlapi-drools-engine" % "2.0.4"
