package hello

import org.scalatest.funsuite.AnyFunSuite
import scalus.compiler.Options
import scalus.uplc.PlutusV3
import scalus.uplc.builtin.Data
import scalus.uplc.builtin.Data.toData
import scalus.uplc.eval.ProfileFormatter
import scalus.cardano.ledger.*
import scalus.cardano.node.Emulator
import scalus.cardano.txbuilder.RedeemerPurpose.ForSpend
import scalus.cardano.txbuilder.{TxBuilder, txBuilder}
import scalus.cardano.onchain.plutus.v1.PubKeyHash
import scalus.testing.kit.Party.Alice
import scalus.testing.kit.TestUtil
import scalus.testing.kit.TestUtil.getScriptContextV3
import scalus.testing.kit.ScalusTest
import scalus.utils.await

class HelloCardanoTest extends AnyFunSuite, ScalusTest {

    private given Options = Options.default
    private given env: CardanoInfo = TestUtil.testEnvironment

    private val contract = PlutusV3.compile(HelloCardano.validate)
    private val scriptAddress = contract.address(env.network)

    // Enabled by SCALUS_PROFILE=1 (or -Dscalus.profile=true); writes target/profile.html.
    private val profilingEnabled =
        sys.env
            .get("SCALUS_PROFILE")
            .orElse(sys.props.get("scalus.profile"))
            .exists(v => v == "1" || v.equalsIgnoreCase("true"))

    test("Hello Cardano message is signed by the owner") {
        val provider = Emulator.withAddresses(Seq(Alice.address))

        // Lock a UTxO at the contract, storing the owner's pub key hash as datum.
        val fundingUtxos =
            provider.findUtxos(Alice.address).await().toOption.get
        val lockTx = TxBuilder(env)
            .payTo(scriptAddress, Value.ada(10), PubKeyHash(Alice.addrKeyHash))
            .complete(availableUtxos = fundingUtxos, sponsor = Alice.address)
            .sign(Alice.signer)
            .transaction
        assert(provider.submit(lockTx).await().isRight)
        val lockedUtxo = Utxo(lockTx.utxos.find { case (_, out) =>
            out.address == scriptAddress
        }.get)

        // Build a draft spending transaction and derive its script context.
        val message: Data = "Hello, Cardano!".toData
        val scriptContext = txBuilder
            .spend(lockedUtxo, message, contract)
            .requireSignature(Alice.addrKeyHash)
            .payTo(Alice.address, lockedUtxo.output.value)
            .draft
            .getScriptContextV3(provider.utxos, ForSpend(lockedUtxo.input))

        val result = contract.program.runWithDebug(scriptContext)
        assert(result.isSuccess)

        if profilingEnabled then
            contract.program.runWithProfile(scriptContext).profile.foreach {
                p =>
                    ProfileFormatter.writeHtml(
                      p,
                      "target/profile.html",
                      title = "HelloCardano"
                    )
                    info("Wrote profile to target/profile.html")
            }
    }
}
