# 0015. Block tool rules are declared once

Status: Accepted

## Context
A block's tool and tier lived in three places that nothing tied together: the properties in `ModBlocks` (`strength`, `requiresCorrectToolForDrops`, often inherited silently from the vanilla block passed to `copy`), the `mineable/*` tags and the `needs_*` tags, both written by hand in `ModBlockTagGenerator`. An audit of the 141 blocks found 19 that dropped nothing (they required a tool but no tool could harvest them), whole families with different tiers per variant, and the tier order of the mod tools contradicting the progression (rhnull is obtained after sanguinite and blasphemite).

## Decision
`BlockToolRules` is the single table: every mod block appears once with a tool (`PICKAXE`, `AXE`, `SHOVEL`, `HOE` or `NONE`) and a tier (`ANY`, `STONE`, `IRON`, `SANGUINITE`, `BLASPHEMITE`, `RHNULL`). `ModBlockTagGenerator` generates the `mineable/*` and `needs_*` tags from it and throws if a registered block has no rule. The test world (`BlockAudit`) logs an error when the resolved tags or `requiresCorrectToolForDrops` of a block disagree with its rule, and `ResourceIntegrityTest.everyBlockHasAToolRule` fails when a block constant is missing from the table. Family hardness uses shared constants in `ModBlocks` (`STONE_HARDNESS`, `ANCIENT_HARDNESS`).
The tool tiers are ordered sanguinite, blasphemite, rhnull.

## Consequences
Adding a block means adding it to `BlockToolRules`, or the test, the datagen and the test world fail. A rule with a tier needs `requiresCorrectToolForDrops()` in the block properties, and the audit says so when it is missing. Blocks that no tool can harvest on purpose (the Seliora resting block) are listed in `BlockAudit.UNHARVESTABLE_ON_PURPOSE`. The properties are still written in `ModBlocks`: deriving them from the rule would need every registration to go through one helper, which can be done later if it keeps going wrong.
