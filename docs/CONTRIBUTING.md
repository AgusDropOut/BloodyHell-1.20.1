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

## GeckoLib content reuses the bases
Anything animated with GeckoLib starts from a base class and a generic renderer, so there is one copy of the cache, the tick, the registration and the asset names. Before writing a renderer or a model class, check the generic ones below; two classes that differ only by names or numbers belong in a table or a builder, not in two files.
- Items: `BaseGeckoItem` for plain items, `BaseGeckoArmorItem` for armor (one `Look` per set: asset name, animated, glowing), `BaseMeleeComboWeapon` for combo weapons (it extends `SwordItem`, so it implements `GeckoAsset` and uses the same generic renderer), `GeckoBlockItem` for animated blocks. The assets come from `getId()`: `geo/<id>.geo.json`, `textures/item/<id>.png` (`textures/armor/<id>.png` for armor) and `animations/<id>.animation.json`.
- Spell books are one line of `SpellBookItems`. A staff look (model and orb effect) is one entry of `StaffRenderers`. Neither needs a renderer or a model class.
- Block entities extend `BaseGeckoBlockEntity` and implement `getAssetPathName()`; the model is `BaseGeckoBlockModel`. The exceptions are the ones that already extend another mod base (pipes, altars) and the Seliora resting block, whose texture name does not follow the convention.
- Entities are registered in `ModRenderers` with `ConfigurableGeoEntityRenderer.builder(...)` and `ConfigurableGeoModel`. Write an entity renderer or model class only when it holds logic: a custom animation, a shader effect or an extra layer.
- `GeckoBaseRulesTest` fails when a new class implements `GeoItem` or `GeoBlockEntity` outside these bases, and `GeckoAssetGameTests` fails when an item has no geo, texture or animation file for its id.

