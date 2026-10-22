# Contributing

## Data generation
- Prefer data generators over hand-written resource or data files (blockstates, models, recipes, tags, loot tables, lang).
- Everything under `src/generated/resources` is produced only by `./gradlew runData` and is never edited by hand.
- A hand-written file is acceptable only when it is too custom for a generator (for example GeckoLib geo and animations, shaders, particle definitions, calibrated item model displays). Say why in the commit message when adding one.

## Animated blocks
- A GeckoLib block is registered with `registerBlockWithoutBlockItem` and gets one `*_item` registered in `ModItems` with `registerGeckoBlockItem(itemName, block, geoId, glowingLayer)`. The geo, texture and animation files are named after `geoId`; pass a texture name as well when the texture file differs (see `seliora_resting_item`). There is one generic class, `GeckoBlockItem`, so no new item class is needed. The item carries the renderer used in the inventory, hand and recipes; the block model is only used when the block is placed.
- Loot tables, recipes, tags and JEI reference the `*_item`, never the block.

## New blocks
- Add every block to `BlockToolRules` with its tool and tier. The tags are generated from it; `./gradlew test`, `runData` and the test world fail if a block is missing or if its properties disagree with the rule.
- A block with a tier must call `requiresCorrectToolForDrops()` in its properties. Do not rely on what `BlockBehaviour.Properties.copy` inherits from the vanilla block: set `strength` and `requiresCorrectToolForDrops()` explicitly.
- Blocks of one family (base, stairs, slab, fence, gate, wall) share the same hardness and resistance. Use the constants in `BlockStrength`.
- Tool tier order, lowest to highest: wood, stone, iron, diamond, netherite, sanguinite, blasphemite, rhnull.

## Registering blocks and items
- Blocks go in a group class in `block/registry` (`StoneBlocks`, `WoodBlocks`, `PlantBlocks`, `MachineBlocks`...), items in `item/registry`. Pick the group by what the thing is; if none fits, add a group and call its `init()` from `ModBlocks.register` or `ModItems.register`.
- Do not put anything else in `ModBlocks` or `ModItems`: no sound types, no anonymous subclasses, no helper data. Give it its own class.
- Do not add classification comments; the group name is the classification.
