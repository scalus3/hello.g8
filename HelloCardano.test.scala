package hello

import org.scalatest.funsuite.AnyFunSuite
import scalus.compiler.compile
import scalus.uplc.builtin.ByteString.*
import scalus.uplc.builtin.Data.toData
import scalus.cardano.onchain.plutus.v1.PubKeyHash
import scalus.cardano.onchain.plutus.prelude.*
import scalus.testing.kit.ScalusTest

class HelloCardanoTest extends AnyFunSuite with ScalusTest {

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

        val result = compile(HelloCardano.validate).runScript(context)
        assert(result.isSuccess)
    }
}
