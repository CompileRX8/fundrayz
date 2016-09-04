name := """fundrayz"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(SbtWeb)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  cache,
  ws,
  filters,
  "org.postgresql" % "postgresql" % "9.4.1207",
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "angularjs" % "1.4.9",
  "org.webjars" % "bootstrap" % "3.3.5",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.5-P24",
  specs2 % Test
)

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

CoffeeScriptKeys.bare := true

LessKeys.strictMath in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

herokuAppName in Compile := "fundrayz"

pipelineStages := Seq(rjs, digest, gzip)

RjsKeys.mainModule := "app"

//fork in run := true
