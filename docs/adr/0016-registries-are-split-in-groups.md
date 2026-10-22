# 0016. Block and item registries are split in logical groups

Status: Accepted

## Context
`ModBlocks` and `ModItems` held about 140 and 160 constants separated by comment headers, mixed with code that has nothing to do with registering: a sound type, strength constants, anonymous block subclasses and the fluid particle setup.

## Decision
`ModBlocks` and `ModItems` only keep the `DeferredRegister`, the registration helpers (`registerBlock`, `registerBlockWithoutBlockItem`, `registerGeckoBlockItem`) and `register(IEventBus)`. The constants live in group classes in `block/registry` (`StoneBlocks`, `WoodBlocks`, `PlantBlocks`, `MachineBlocks`...) and `item/registry` (`SanguiniteItems`, `SpellBookItems`, `FoodItems`...). Each group has an empty `init()`; `register(IEventBus)` calls every `init()` so the static initializers run before the registry event. Code outside the registries is in its own class (`ModSoundTypes`, `BlockStrength`, `FlammablePlanksBlock`, `BloodWallMushroomBlock`).
A group name replaces a classification comment, so groups carry no comments.

## Consequences
Referencing `StoneBlocks.X` initializes only that group, so a new group must be added to the `init()` calls in `ModBlocks` or `ModItems`, otherwise its entries are never registered. `ModItems.ITEMS` and `ModBlocks.BLOCKS` must stay the first fields of their classes, and neither class may reference a group during its own class initialization, or the two registries would initialize each other. `ResourceIntegrityTest` reads the group files, so a new registration helper needs a new pattern there.
