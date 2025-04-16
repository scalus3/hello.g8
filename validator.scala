//> using scala 3.3.5
//> using options -deprecation -feature
//> using plugin org.scalus:scalus-plugin_3:0.8.5+380-34c30a02-SNAPSHOT
//> using dep org.scalus:scalus_3:0.8.5+380-34c30a02-SNAPSHOT
package validator

import scalus.*
import scalus.builtin.Data
import scalus.builtin.FromDataInstances.given
import scalus.ledger.api.v1.FromDataInstances.given
import scalus.ledger.api.v3.{PubKeyHash, TxInfo, TxOutRef}
import scalus.prelude.*
import scalus.prelude.Option.Some
import scalus.prelude.Prelude.*

/** A simple validator that checks if the redeemer is "Hello, Cardano!" and if the transaction is signed by the owner.
  */
@Compile
object HelloCardano extends Validator {
    override def spend(
        datum: Option[Data],
        redeemer: Data,
        tx: TxInfo,
        sourceTxOutRef: TxOutRef
    ): Unit = {
        val Some(ownerData) = datum: @unchecked
        val owner = ownerData.to[PubKeyHash]
        val signed = tx.signatories.contains(owner)
        require(signed, "Must be signed")
        val saysHello = redeemer.to[String] == "Hello, Cardano!"
        require(saysHello, "Invalid redeemer")
    }
}
