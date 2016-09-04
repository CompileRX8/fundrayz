name := "postgresExtension"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4.1209",
  "com.github.tminglei" %% "slick-pg" % "0.14.3",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.14.3",
  specs2 % Test
)
