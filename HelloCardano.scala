package hello

import scalus.compiler.Compile
import scalus.uplc.builtin.Data
import scalus.cardano.onchain.plutus.v3.*
import scalus.cardano.onchain.plutus.prelude.*

/** This validator demonstrates two key validation checks:
  *   1. It verifies that the transaction is signed by the owner's public key
  *      hash (stored in the datum).
  *   1. It confirms that the redeemer contains the exact string "Hello,
  *      Cardano!".
  *
  * Both conditions must be met for the validator to approve spending the UTxO.
  */
@Compile
object HelloCardano extends Validator {
    inline override def spend(
        datum: Option[Data],
        redeemer: Data,
        tx: TxInfo,
        ownRef: TxOutRef
    ): Unit = {
        val owner = datum.getOrFail("Datum not found").to[PubKeyHash]
        require(tx.isSignedBy(owner), "Must be signed")
        val saysHello = redeemer.to[String] == "Hello, Cardano!"
        require(saysHello, "Invalid redeemer")
    }
}
