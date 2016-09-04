name := """monolith"""

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

