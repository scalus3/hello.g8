package hello

import org.scalatest.funsuite.AnyFunSuite
import scalus.compiler.Options
import scalus.uplc.PlutusV3
import scalus.uplc.builtin.ByteString.*
import scalus.uplc.builtin.Data.toData
import scalus.cardano.onchain.plutus.v1.PubKeyHash
import scalus.cardano.onchain.plutus.prelude.*
import scalus.testing.kit.ScalusTest

class HelloCardanoTest extends AnyFunSuite with ScalusTest {

    private given Options = Options.default

    private val compiled = PlutusV3.compile(HelloCardano.validate)

    test("Hello Cardano message is signed by the owner") {
        val ownerPubKey = PubKeyHash(
          hex"1234567890abcdef1234567890abcdef1234567890abcdef12345678"
        )
        val message = "Hello, Cardano!".toData
        val context = makeSpendingScriptContext(
          datum = ownerPubKey.toData,
          redeemer = message,
          signatories = List(ownerPubKey)
        )

        val result = compiled.program.runWithDebug(context)
        assert(result.isSuccess)
    }
}
