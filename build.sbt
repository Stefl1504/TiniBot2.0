// (5) shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

name := "TiniBot2.0"
version := "2.0"

val scalaV = "2.12.2"

scalaVersion := scalaV

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Twitter Maven" at "http://maven.twttr.com"
resolvers += "jcenter" at "http://jcenter.bintray.com"

val http4sVersion = "0.15.13a"
val akkaVersion = "2.5.3"

lazy val root = project
  .in(file("."))
  .aggregate(bot, web, jsui)
  .settings(
    scalaVersion := scalaV,
    name := "root")

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full) // [Pure, Full, Dummy], default: CrossType.Full.in(file("shared"))
  .in(file("."))
  .settings(
    name := "tini-shared",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats" % "0.9.0"
    ))

lazy val sharedJvm = shared.jvm
  //.in(file("sharedJvm"))
  .settings(
  scalaVersion := scalaV,
  name := "tini-sharedJvm",
  libraryDependencies ++= Seq(
    //"eu.unicredit" %% "shocon" % "0.1.8"
    "com.github.pureconfig" %% "pureconfig" % "0.7.2",
    "com.chuusai" %% "shapeless" % "2.3.2",
    "com.typesafe.akka" %% "akka-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http" % "10.0.9" ))

lazy val sharedJs = shared.js
  //.in(file("sharedJs"))
  .settings(
  scalaVersion := scalaV,
  name := "tini-sharedJs")

lazy val akkaCord = ProjectRef(uri("git://github.com/Katrix-/AkkaCord.git"), "akkaCord")
lazy val bot = project
  .in(file("bot"))
  .settings(
    scalaVersion := scalaV,
    name := "tini-bot",
    resolvers += "jcenter-bintray" at "http://jcenter.bintray.com",
    resolvers += "jitpack.io" at "https://jitpack.io",
    resolvers += "javacord-repo" at "http://repo.bastian-oppermann.de",
    libraryDependencies ++= Seq(
      "net.dv8tion" % "JDA" % "3.1.0_204",
      "com.github.austinv11" % "Discord4J" % "2.8.4",
      "de.btobastian.javacord" % "javacord" % "2.0.14",
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      // Optional for auto-derivation of JSON codecs
      "io.circe" %% "circe-generic" % "0.6.1",
      // Optional for string interpolation to JSON model
      "io.circe" %% "circe-literal" % "0.6.1"
    ))
  .dependsOn(sharedJvm)
  .dependsOn(akkaCord)

lazy val web = project
  .in(file("web"))
  .settings(
    scalaVersion := scalaV,
    name := "tini-web",
    libraryDependencies ++= Seq(
      "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"     %% "http4s-circe"        % http4sVersion,
      "org.http4s"     %% "http4s-dsl"          % http4sVersion))
  .dependsOn(sharedJvm)

lazy val jsui = project
  .in(file("jsui"))
  .settings(
    scalaVersion := scalaV,
    name := "tini-jsui")
  .dependsOn(sharedJs)








