
val playVersion = "[2.6,2.7["
val playClassifier = "-play26"

name := "aylien-client" + playClassifier

organization := "com.kinja"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-ws" % playVersion

