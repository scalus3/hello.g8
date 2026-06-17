lazy val root = (project in file("."))
    .enablePlugins(ScalusBlueprintPlugin)
    .settings(
      scalaVersion := "3.3.7",
      scalacOptions ++= Seq("-deprecation", "-feature"),
      libraryDependencies ++= Seq(
        "org.scalus" %% "scalus" % "0.18.1",
        "org.scalus" %% "scalus-cardano-ledger" % "0.18.1",
        "org.scalus" %% "scalus-testkit" % "0.18.1" % Test,
        "org.scalatest" %% "scalatest" % "3.2.20" % Test
      ),
      addCompilerPlugin(
        ("org.scalus" % "scalus-plugin" % "0.18.1").cross(CrossVersion.full)
      ),
      // Scala CLI-compatible flat layout: sources live at the project root, and
      // test sources use the `.test.scala` suffix.
      Compile / sources := (baseDirectory.value * "*.scala")
          .get()
          .toSeq
          .filterNot(_.getName.endsWith(".test.scala")),
      Test / sources := (baseDirectory.value * "*.test.scala").get().toSeq
    )
