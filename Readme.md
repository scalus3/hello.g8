# Scalus Hello Cardano Smart Contract

A minimal [Scalus](https://scalus.org) smart contract, generated from the `hello.g8` template.

`HelloCardano.scala` is a Plutus V3 spending validator that approves spending a UTxO only when the
transaction is both signed by the owner stored in the datum **and** carries the redeemer string
`"Hello, Cardano!"`.

## Prerequisites

- A JDK (17+)
- [Scala CLI](https://scala-cli.virtuslab.org/)

## Build & test

```sh
scala-cli test .
```

```sh
scala-cli compile .
```

Format the sources with:

```sh
scala-cli fmt .
```

## Learn more

- Scalus documentation: https://scalus.org
- Examples: https://github.com/nau/scalus/tree/master/scalus-examples
