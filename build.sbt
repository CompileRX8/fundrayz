name := """fundrayz"""

lazy val commonSettings = Seq(
  organization := "net.highley.fundrayz",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val databaseDependencies = Seq(
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4.1209",
  "com.github.tminglei" %% "slick-pg" % "0.14.3",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.14.3"
)

lazy val webDependencies = Seq(
  filters,
  "org.webjars" %% "webjars-play" % "2.5.0-3",
  "org.webjars" % "angularjs" % "1.5.8",
  "org.webjars" % "bootstrap" % "3.3.7-1"
)

lazy val webClientDependencies = Seq(
  cache,
  ws
)

lazy val postgresExtension = (project in file("modules/postgresExtension"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= databaseDependencies)
  .enablePlugins(PlayScala)
  .dependsOn()

lazy val profile = (project in file("modules/profile"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn(postgresExtension)

lazy val security = (project in file("modules/security"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= webClientDependencies)
  .enablePlugins(PlayScala)
  .dependsOn(postgresExtension)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .dependsOn(profile, postgresExtension, security)
  .aggregate(profile, postgresExtension, security)

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

//pipelineStages := Seq(rjs, digest, gzip)

