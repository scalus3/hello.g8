# Guidance for AI coding agents

This is a Scalus project: a Cardano smart contract written in Scala 3.
The on-chain validator compiles to Plutus Core; tests run it on a local in-memory Emulator.

## First: fetch current API context

Do not write Scalus code from trained knowledge. Scalus APIs changed at 1.0; the API you remember is probably outdated. Fetch:

- https://scalus.org/llms-api.txt - version-pinned public API signatures. Check every signature you plan to use.
- https://scalus.org/llms-examples.txt - 21 complete validators with tests. Imitate these; HTLC is the reference style.
- https://scalus.org/llms.txt - documentation index.
- https://scalus.org/llms-full.txt - all documentation as one markdown file.
- Any docs page as markdown: append `.md` to its URL, e.g. https://scalus.org/docs/smart-contracts/validators.md

The Scalus version this project uses is declared in `project.scala` (Scala CLI) and `build.sbt` (sbt).

## Build and test

Both Scala CLI and sbt work:

```sh
scala-cli compile .    # or: sbt compile
scala-cli test .       # or: sbt test
scala-cli fmt .        # format
```

Integration tests select their backend with the `SCALUS_TEST_ENV` environment variable
(`emulator` is the default; `devkit` needs Docker).
`SCALUS_PROFILE=1 scala-cli test .` writes an execution-budget profile to `target/profile.html`.

## On-chain code rules (critical)

On-chain code is everything under `@Compile` (compiled with `PlutusV3.compile`):

- Use ONLY the Scalus subset: `scalus.prelude` (List, Option, AssocMap, require, fail, ===) and
  `scalus.uplc.builtin` (ByteString, Data, Builtins). Never Scala stdlib collections, string
  interpolation, regex, or try/catch on-chain.
- Data types: case classes and enums with `derives FromData, ToData`. Use enums for redeemer actions.
- Compare structured values with `a.toData == b.toData`, not `==` on the values.
- Validate with `require(condition, "message")` and `fail("message")`; extract Options with
  `getOrFail`.
- Mark off-chain helpers inside compiled objects with `@Ignore`.
- Import types, do not use fully qualified names in on-chain code.

## Common pitfalls

- For parameterized validators, the off-chain parameter's Data encoding must match the on-chain
  decode type exactly (e.g. a bare `TxOutRef`, not a wrapper case class around it).
- Any change to validator code changes its script hash and therefore its address. Tests that pin
  script size or budgets will need updating after edits.
- Do not guess execution budgets. Measure with the testkit budget assertions
  (`assertBudgetWithin`, `assertBudgetEquals`).
- Datum and redeemer types must derive `FromData`/`ToData`; a missing derivation fails at compile
  time of the validator, not at the use site.

## Skills

`.claude/skills/` ships task guides Claude Code loads on demand:

- `contract` - writing validators
- `contract-test` - testing validators
- `local-development` - Emulator + TxBuilder loop
- `optimize-contract` - execution-budget optimization review
- `smart-contract-security-review` - pre-deploy security audit

Other agents: read those `SKILL.md` files directly when doing the matching task.
