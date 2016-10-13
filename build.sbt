
name := "aylien-client-play25"

organization := "com.kinja"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.8"

initialCommands in console := """import scala.concurrent.ExecutionContext.Implicits.global; import play.api.libs.json._; import com.kinja.play.aylien.textapi._, model._; val config = TextApiClientConfig(new java.net.URL("http://api.aylien.com/api/v1"), System.getenv("AYLIEN_APP_ID"), System.getenv("AYLIEN_APP_KEY"), "iptc-subjectcode", scala.concurrent.duration.Duration("30 seconds"), play.api.libs.ws.ning.NingWSClient(), scala.concurrent.ExecutionContext.Implicits.global)"""
