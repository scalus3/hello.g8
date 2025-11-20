package validator

import scalus.*
import scalus.builtin.Data
import scalus.ledger.api.v3.{PubKeyHash, TxInfo, TxOutRef}
import scalus.prelude.*

/** This validator demonstrates two key validation checks:
    * 1. It verifies that the transaction is signed by the owner's public key hash (stored in the datum)
    * 2. It confirms that the redeemer contains the exact string "Hello, Cardano!"
    * 
    * Both conditions must be met for the validator to approve spending the UTxO.
      */

@Compile
object HelloCardano extends Validator:
    inline override def spend(
        datum: Option[Data],
        redeemer: Data,
        tx: TxInfo,
        outRef: TxOutRef
    ): Unit =
        val owner = datum.getOrFail("Datum not found").to[PubKeyHash]
        val signed = tx.signatories.contains(owner)
        require(signed, "Must be signed")
        val saysHello = redeemer.to[String] == "Hello, World!"
        require(saysHello, "Invalid redeemer")

