package hello

import scalus.cardano.blueprint.{Blueprint, Contract}
import scalus.cardano.onchain.plutus.v3.PubKeyHash
import scalus.compiler.Options
import scalus.uplc.PlutusV3

/** Bundles the compiled validator with a CIP-57 blueprint.
  *
  * The Scalus compiler plugin discovers `Contract` objects, and the
  * `scalus-sbt-plugin` exposes them to the `blueprint` and `deploy` sbt tasks.
  */
object HelloCardanoContract extends Contract {
    private given Options = Options.release
    lazy val compiled = PlutusV3.compile(HelloCardano.validate)
    lazy val blueprint = Blueprint.plutusV3[PubKeyHash, String](
      title = "Hello Cardano",
      description =
          "Checks the redeemer is \"Hello, Cardano!\" and the tx is signed by the owner.",
      version = "1.0.0",
      license = Some("Apache License Version 2.0"),
      compiled = compiled
    )
}
