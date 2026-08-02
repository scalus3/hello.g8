lazy val root = (project in file("."))
    .enablePlugins(ScalusBlueprintPlugin)
    .settings(
      scalaVersion := "3.3.8",
      scalacOptions ++= Seq("-deprecation", "-feature"),
      libraryDependencies ++= Seq(
        "org.scalus" %% "scalus" % "1.0.0",
        "org.scalus" %% "scalus-cardano-ledger" % "1.0.0",
        "org.scalus" %% "scalus-testkit" % "1.0.0" % Test,
        "org.scalatest" %% "scalatest" % "3.2.20" % Test
      ),
      addCompilerPlugin(
        ("org.scalus" % "scalus-plugin" % "1.0.0").cross(CrossVersion.full)
      ),
      Compile / sources := (baseDirectory.value * "*.scala")
          .get()
          .toSeq
          .filterNot(_.getName.endsWith(".test.scala")),
      Test / sources := (baseDirectory.value * "*.test.scala").get().toSeq
    )
