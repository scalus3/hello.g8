# Scalus Hello Cardano Smart Contract

A minimal [Scalus](https://scalus.org) smart contract, generated from the `hello.g8` template.

`HelloCardano.scala` is a Plutus V3 spending validator that approves spending a UTxO only when the
transaction is both signed by the owner stored in the datum **and** carries the redeemer string
`"Hello, Cardano!"`.

## Prerequisites

- A JDK (17+)
- [Scala CLI](https://scala-cli.virtuslab.org/) and/or [sbt](https://www.scala-sbt.org/)

## Build & test

The project works with both Scala CLI and sbt.

With Scala CLI:

```sh
scala-cli test .
scala-cli compile .
scala-cli fmt .
```

With sbt:

```sh
sbt test
sbt compile
```

Both tools share the same flat source layout: sources live at the project root and test
sources use the `.test.scala` suffix.

## Learn more

- Scalus documentation: https://scalus.org
- Examples: https://github.com/nau/scalus/tree/master/scalus-examples
