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

### Test backends

`HelloCardanoIntegration.test.scala` runs against a backend selected by the `SCALUS_TEST_ENV`
environment variable:

```sh
sbt test                       # in-memory emulator (default, fast)
SCALUS_TEST_ENV=yaci sbt test  # a local Yaci DevKit node (requires a running Docker daemon)
```

The same works with Scala CLI, e.g. `SCALUS_TEST_ENV=yaci scala-cli test .`.

## Blueprint & deploy

The sbt build enables the [`scalus-sbt-plugin`](https://github.com/nau/scalus), which adds tasks
backed by the `Contract` in `HelloCardanoContract.scala`:

```sh
# Generate a CIP-57 blueprint at
# target/scala-3.3.7/classes/META-INF/scalus/blueprints/HelloCardanoContract.json
sbt blueprint

# Deploy the validator as a reference-script UTxO (needs a Blockfrost key and a funded wallet)
export BLOCKFROST_API_KEY=...        # or pass --blockfrost-key
export CARDANO_MNEMONIC="word1 ..."  # or pass --mnemonic
sbt "deploy HelloCardanoContract --network preview"
```

## Profiling

Set `SCALUS_PROFILE=1` to run the tests with on-chain execution profiling enabled. It writes an
interactive HTML profile (CPU/memory per source line) to `target/profile.html`:

```sh
SCALUS_PROFILE=1 sbt test
```

The same works with Scala CLI, e.g. `SCALUS_PROFILE=1 scala-cli test .`.

## Learn more

- Scalus documentation: https://scalus.org
- Examples: https://github.com/nau/scalus/tree/master/scalus-examples
