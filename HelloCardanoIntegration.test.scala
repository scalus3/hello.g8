package hello

import org.scalatest.funsuite.AnyFunSuite
import scalus.compiler.Options
import scalus.uplc.PlutusV3
import scalus.uplc.builtin.Data.toData
import scalus.cardano.ledger.*
import scalus.cardano.txbuilder.TxBuilder
import scalus.cardano.onchain.plutus.v1.PubKeyHash
import scalus.testing.integration.{
    IntegrationTest,
    IntegrationTestContext,
    TestParty
}
import scalus.utils.await

import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Try

/** Locks a UTxO at the contract and submits a spend to the selected backend.
  *
  * Backend is chosen by `scalus.testEnv` (sbt `emulator`/`devkit` tasks) or the
  * SCALUS_TEST_ENV env var: "emulator" (default, in-memory) or "yaci" (a local
  * Yaci DevKit node, requires Docker).
  */
class HelloCardanoIntegrationTest extends AnyFunSuite, IntegrationTest {

    override protected lazy val testEnvName: String =
        sys.props
            .get("scalus.testEnv")
            .orElse(sys.env.get("SCALUS_TEST_ENV"))
            .getOrElse("emulator")
            .toLowerCase
            .trim

    private given Options = Options.release
    private val contract = PlutusV3.compile(HelloCardano.validate)

    /** Lock 10 ada at the contract owned by `owner`, then try to spend it as
      * `spender` with the "Hello, Cardano!" redeemer. Returns whether the node
      * accepted the spend.
      */
    private def lockThenSpend(
        ctx: IntegrationTestContext,
        owner: TestParty,
        spender: TestParty
    ): Boolean = {
        val scriptAddress = contract.address(ctx.cardanoInfo.network)

        val funding = ctx.provider.findUtxos(owner.address).await().toOption.get
        val lockTx = TxBuilder(ctx.cardanoInfo)
            .payTo(scriptAddress, Value.ada(10), PubKeyHash(owner.addrKeyHash))
            .complete(availableUtxos = funding, sponsor = owner.address)
            .sign(owner.signer)
            .transaction
        assert(ctx.submit(lockTx).await().isRight)

        val lockedUtxo =
            Utxo(
              ctx.provider.findUtxos(scriptAddress).await().toOption.get.head
            )
        val spenderUtxos =
            ctx.provider.findUtxos(spender.address).await().toOption.get

        Try {
            val spendTx = TxBuilder(ctx.cardanoInfo)
                .spend(lockedUtxo, "Hello, Cardano!".toData, contract)
                .requireSignature(spender.addrKeyHash)
                .payTo(spender.address, lockedUtxo.output.value)
                .complete(
                  availableUtxos = spenderUtxos,
                  sponsor = spender.address
                )
                .sign(spender.signer)
                .transaction
            ctx.submit(spendTx).await()
        }.toOption.exists(_.isRight)
    }

    test(s"[\$testEnvName] owner can spend with the Hello redeemer") {
        val ctx = createTestContext()
        assert(lockThenSpend(ctx, owner = ctx.alice, spender = ctx.alice))
    }

    test(s"[\$testEnvName] a non-owner cannot spend") {
        val ctx = createTestContext()
        assert(!lockThenSpend(ctx, owner = ctx.alice, spender = ctx.bob))
    }
}
