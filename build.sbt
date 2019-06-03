name := """play-medbot-rest"""
organization := "com.piumalkulasekara"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"
resolvers += "JBoss" at "https://repository.jboss.org/"

libraryDependencies += guice
libraryDependencies += "com.typesafe" % "config" % "1.3.4"

// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-bom
libraryDependencies += "com.amazonaws" % "aws-java-sdk-bom" % "1.11.562" pomOnly()

// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.562"

// https://mvnrepository.com/artifact/edu.stanford.swrl/swrlapi
libraryDependencies += "edu.stanford.swrl" % "swrlapi" % "2.0.6"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.11"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.11.3"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.11"

// https://mvnrepository.com/artifact/edu.stanford.swrl/swrlapi-drools-engine
libraryDependencies += "edu.stanford.swrl" % "swrlapi-drools-engine" % "2.0.6"

libraryDependencies += "com.google.cloud" % "google-cloud-dialogflow" % "0.94.0-alpha"
