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
      Compile / sources := (baseDirectory.value * "*.scala")
          .get()
          .toSeq
          .filterNot(_.getName.endsWith(".test.scala")),
      Test / sources := (baseDirectory.value * "*.test.scala").get().toSeq,
      commands ++= Seq(
        Command.command("profile") { st =>
            System.setProperty("scalus.profile", "true"); "testFull" :: st
        },
        Command.command("emulator") { st =>
            System.setProperty("scalus.testEnv", "emulator"); "testFull" :: st
        },
        Command.command("devkit") { st =>
            System.setProperty("scalus.testEnv", "yaci"); "testFull" :: st
        }
      )
    )
