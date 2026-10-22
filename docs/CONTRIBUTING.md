# Contributing

## Data generation
- Prefer data generators over hand-written resource or data files (blockstates, models, recipes, tags, loot tables, lang).
- Everything under `src/generated/resources` is produced only by `./gradlew runData` and is never edited by hand.
- A hand-written file is acceptable only when it is too custom for a generator (for example GeckoLib geo and animations, shaders, particle definitions, calibrated item model displays). Say why in the commit message when adding one.

## Animated blocks
- A GeckoLib block is registered with `registerBlockWithoutBlockItem` and gets one `*_item` that extends `BaseGeckoBlockItem` in `ModItems`. The item carries the renderer used in the inventory, hand and recipes; the block model is only used when the block is placed.
- Loot tables, recipes, tags and JEI reference the `*_item`, never the block.
