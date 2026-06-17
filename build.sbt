val profile = taskKey[Unit](
  "Run tests with on-chain profiling enabled; writes an HTML report under target/"
)
val emulator =
    taskKey[Unit]("Run tests against the in-memory Emulator (default)")
val devkit = taskKey[Unit](
  "Run tests against a local Yaci DevKit node (requires Docker)"
)

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
      Test / sources := (baseDirectory.value * "*.test.scala").get().toSeq,
      // `sbt profile` runs the tests with profiling on (see *.test.scala).
      profile := Def.taskDyn {
          System.setProperty("scalus.profile", "true")
          (Test / test)
      }.value,
      // `sbt emulator` / `sbt devkit` pick the integration-test backend.
      emulator := Def.taskDyn {
          System.setProperty("scalus.testEnv", "emulator")
          (Test / test)
      }.value,
      devkit := Def.taskDyn {
          System.setProperty("scalus.testEnv", "yaci")
          (Test / test)
      }.value
    )
