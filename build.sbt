name := """fundrayz"""

lazy val commonSettings = Seq(
  organization := "net.highley.fundrayz",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(userProfile, postgresExtension, security) //, monolith)

lazy val userProfile = project
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn(postgresExtension)

lazy val postgresExtension = project
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn()

lazy val security = project
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn(postgresExtension)

//lazy val monolith = project
//  .settings(commonSettings: _*)
//  .enablePlugins(PlayScala)
//  .enablePlugins(SbtWeb)

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

