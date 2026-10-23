# 0019. Registry ids are public to modpacks

Status: Proposed

## Context
The Desecrated modpack pins `bloodyhell-0.8.585.jar` and names our ids in its KubeJS scripts (stat changes at startup, shaped recipes), in its FTB Quests chapters, in `emi.json` and in a tag file inside its own `desecratedcompat` jar. None of its Java code references our classes. Renaming the Blood bow, arrow and armor to Sanguinite changed six item ids that all of those places use.

What Forge does with a saved world (`GameData.injectSnapshot` and `ForgeRegistry.processMissingEvent`, Forge 47.4.20): a level.dat stores the ids of 14 registries (block, item, entity type, fluid, mob effect, painting variant, sound event, potion, enchantment, point of interest type, villager profession, memory module type, stat type, biome). Block entity types and particle types are not stored. In a local world, an id that no longer exists is removed with a warning and an automatic world backup; the world loads. A `MissingMappingsEvent` handler can remap an old id to a new one, and the remap keeps an alias for that world.

A remap does not help anything that names an id before a world is loaded: startup scripts, recipes, tags and quests resolve names when the game or the datapacks load, so they need the new id.

## Decision
- Removing content is acceptable: the items and blocks disappear from old worlds and the game does not crash.
- Renaming an id is a breaking change for modpacks. It is done only in a version that the changelog announces, with an entry in `LegacyIdRemapper` so saved worlds convert, and after running `scripts/check-modpack-ids.py` against the known packs so their authors get the list of lines to change.
- Display names can change at any time; ids cannot be renamed for cosmetic reasons inside a stabilization release.

## Consequences
`RegistryDumpGameTests` writes the ids of a build and the script compares a pack against it, so the cost of a rename is known before it ships. The Blood to Sanguinite rename is the open case: see the technical notes of the author.
