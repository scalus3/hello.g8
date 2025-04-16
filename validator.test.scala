//> using scala 3.3.5
//> using options -deprecation -feature
//> using plugin org.scalus:scalus-plugin_3:0.8.5+380-34c30a02-SNAPSHOT
//> using dep org.scalus:scalus_3:0.8.5+380-34c30a02-SNAPSHOT
//> using test.dep org.scalameta::munit::1.1.0
//> using test.dep org.scalameta::munit-scalacheck::1.1.0
//> using test.dep org.scalacheck::scalacheck::1.18.1
package validator

import munit.FunSuite
import scalus.*
import scalus.Compiler.compile
import scalus.builtin.given
import scalus.builtin.ByteString.*
import scalus.builtin.Data.toData
import scalus.builtin.ToDataInstances.given
import scalus.builtin.{ByteString, Data}
import scalus.ledger.api.v1.PubKeyHash
import scalus.ledger.api.v3.*
import scalus.ledger.api.v3.given
import scalus.prelude.*
import scalus.uplc.*
import scalus.uplc.eval.*

import scala.language.implicitConversions
import scala.math.Ordering.Implicits.*
import scalus.ledger.api.v2.OutputDatum

class HelloCardanoSpec extends FunSuite {

    test("Hello Cardano") {
        val ownerPubKey = PubKeyHash(hex"1234567890abcdef1234567890abcdef1234567890abcdef12345678")
        val message = "Hello, Cardano!".toData
        val context = ScriptContext(
          scriptInfo = ScriptInfo.SpendingScript(txOutRef =
              TxOutRef(TxId(hex"1234567890abcdef1234567890abcdef1234567890abcdef12345678"), 0)
          ),
          txInfo = TxInfo(
            inputs = List(
              TxInInfo(
                outRef = TxOutRef(TxId(hex"1234567890abcdef1234567890abcdef1234567890abcdef12345678"), 0),
                resolved = TxOut(
                  address = Address(
                    Credential.PubKeyCredential(
                      PubKeyHash(hex"1234567890abcdef1234567890abcdef1234567890abcdef12345678")
                    ),
                    Option.None
                  ),
                  datum = OutputDatum.OutputDatum(message),
                  value = Value.lovelace(1000000)
                )
              )
            ),
            signatories = List(ownerPubKey),
            id = TxId(hex"1234567890abcdef1234567890abcdef1234567890abcdef12345678")
          )
        )

        given PlutusVM = PlutusVM.makePlutusV3VM()
        val result = compile(HelloCardano.validate).toUplc(true).plutusV3.evaluateDebug

        assert(result.isSuccess)
        assert(result.budget <= ExBudget(ExCPU(62_000000), ExMemory(240000)), clue = result.budget)
    }
}
