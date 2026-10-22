# Contributing

## Data generation
- Prefer data generators over hand-written resource or data files (blockstates, models, recipes, tags, loot tables, lang).
- Everything under `src/generated/resources` is produced only by `./gradlew runData` and is never edited by hand.
- A hand-written file is acceptable only when it is too custom for a generator (for example GeckoLib geo and animations, shaders, particle definitions, calibrated item model displays). Say why in the commit message when adding one.

## Animated blocks
- A GeckoLib block is registered with `registerBlockWithoutBlockItem` and gets one `*_item` registered in `ModItems` with `registerGeckoBlockItem(itemName, block, geoId, glowingLayer)`. The geo, texture and animation files are named after `geoId`; pass a texture name as well when the texture file differs (see `seliora_resting_item`). There is one generic class, `GeckoBlockItem`, so no new item class is needed. The item carries the renderer used in the inventory, hand and recipes; the block model is only used when the block is placed.
- Loot tables, recipes, tags and JEI reference the `*_item`, never the block.
