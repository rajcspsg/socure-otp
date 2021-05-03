val ScalatraVersion = "2.4.0"

organization := "com.socure"

name := "SocureTakeHome"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.28.v20200408" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)

enablePlugins(ScalatraPlugin)
