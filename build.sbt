lazy val root = (project in file("."))
    .enablePlugins(PlayJava, PlayEbean)
    .settings(
        name := """spending-tracker""",
        version := "1.0-SNAPSHOT",
        organization := "com.example",
        scalaVersion := "2.13.14",
        libraryDependencies ++= Seq(
            guice,
            jdbc,
            "com.h2database" % "h2" % "2.2.224"
        )
    )