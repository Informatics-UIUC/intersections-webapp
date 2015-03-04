import play.PlayScala

lazy val commonSettings = Seq(
  scalaVersion := "2.11.5",
  scalacOptions ++= Seq("-feature", "-language:postfixOps", "-target:jvm-1.7"),
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
  resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

lazy val webapp = (project in file(".")).
  enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(
    name := "webapp",
    version := "2.3.1-SNAPSHOT",
    packageDescription := "This is the FB events search webapp for the Intersections project",
    maintainer in Linux := "Boris Capitanu <capitanu@illinois.edu>",
    packageSummary in Linux := "Intersections - FB Events",
    libraryDependencies ++= Seq(
      jdbc,
      anorm,
      cache,
      ws,
      "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
      "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
    )
  )
