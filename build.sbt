import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import play.PlayScala

name := """webapp"""

version := "2.0-SNAPSHOT"

maintainer in Linux := "Boris Capitanu <capitanu@illinois.edu>"

packageSummary in Linux := "Intersections - FB Events"

packageDescription := "This is the FB events search webapp for the Intersections project"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT"
)
