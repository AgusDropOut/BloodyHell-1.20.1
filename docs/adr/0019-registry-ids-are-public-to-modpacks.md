# 0019. Registry ids are public to modpacks

Status: Accepted

## Context
The Desecrated modpack pins `bloodyhell-0.8.585.jar` and names our ids in its KubeJS scripts (stat changes at startup, shaped recipes), in its FTB Quests chapters, in `emi.json` and in a tag file inside its own `desecratedcompat` jar. None of its Java code references our classes. Renaming the Blood bow, arrow and armor to Sanguinite changed six item ids that all of those places use.

What Forge does with a saved world (`GameData.injectSnapshot` and `ForgeRegistry.processMissingEvent`, Forge 47.4.20): a level.dat stores the ids of 14 registries (block, item, entity type, fluid, mob effect, painting variant, sound event, potion, enchantment, point of interest type, villager profession, memory module type, stat type, biome). Block entity types and particle types are not stored. In a local world, an id that no longer exists is removed with a warning and an automatic world backup; the world loads. A `MissingMappingsEvent` handler can remap an old id to a new one, and the remap keeps an alias for that world.

Checked in practice on 2026-10-23: the test world had been saved with items and an entity type that were removed afterwards. The game logged `Unidentified mapping from registry minecraft:item` and `entity_type`, `There are unidentified mappings in this world - we are going to attempt to process anyway` and `Skipping Entity with id ...`, loaded the world and finished the automatic test; the next run was clean. `make testworld` reports those lines as suspicious once after a removal.

A remap does not help anything that names an id before a world is loaded: startup scripts, recipes, tags and quests resolve names when the game or the datapacks load, so they need the new id.

## Decision
- Removing content is acceptable: the items and blocks disappear from old worlds and the game does not crash.
- Renaming an id is a breaking change for modpacks. It is done only in a version that the changelog announces, with an entry in `LegacyIdRemapper` (it exists, `event/handlers/LegacyIdRemapper.java`, and `LegacyIdGameTests` checks its targets) so saved worlds convert, and after running `scripts/check-modpack-ids.py` against the known packs so their authors get the list of lines to change.
- Display names can change at any time; ids cannot be renamed for cosmetic reasons inside a stabilization release.
- The Blood bow, arrow and armor keep their ids (`blood_bow`, `blood_arrow`, `blood_helmet`, `blood_chestplate`, `blood_leggings`, `blood_boots`) and are shown as Sanguinite in the lang file. The full rename (`sanguinite_*`, with a `MissingMappingsEvent` handler for the saved worlds and the pack lines listed by `check-modpack-ids.py`) waits for a release that the pack authors update with us. It was done and reverted on 2026-10-23.

## Consequences
`RegistryDumpGameTests` writes the ids of a build and the script compares a pack against it, so the cost of a rename is known before it ships. The Blood to Sanguinite rename shows the cost: six ids named in four places of the Desecrated pack, one of them a compiled jar.
