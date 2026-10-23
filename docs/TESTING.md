# Testing

## Automated test world

Generates every block, entity and item of the mod in an organized grid, then scans the log for errors.

One-time setup: create a singleplayer world named `BHTestWorld` (Creative, Allow Cheats ON, Superflat).

Shortcuts (`Makefile`): `make compile`, `make build`, `make test`, `make gametest`, `make testworld` (`EXIT_TICKS=600`). `make help` lists them.

Run:
- `scripts/run-testworld.ps1` launches the client (`gradlew runClient -PbhAutoTest`), joins `BHTestWorld`, waits for the world to load, runs `/bhtest build`, closes the client after `-ExitTicks` ticks (default 600) and prints the `[BHTEST]` summary plus every suspicious log line. Exit code 2 means new problems were found, 1 means the run itself failed. Do not close the game window while it runs.
- `/bhtest clear` removes every non player entity and block in the test area plus a margin, and `/bhtest build` does it first, so repeated runs do not pile up entities or fluids.
- Inside any world with cheats, `/bhtest build` builds the same layout anchored at the world spawn (not at the player, so every run reuses and clears the same area), in this order along +Z: blocks, entities (AI disabled), chests with every item.
- The script needs JDK 17. It sets `JAVA_HOME` itself, because the system default may be a newer JDK that Gradle 8.1.1 cannot run on.

Every build also writes `run/bh-block-audit.csv` (`BlockAudit`, devtools): per mod block the real hardness, blast resistance, `requiresCorrectToolForDrops`, whether a loot table exists, and the `mineable/*` and `needs_*` tags as the game resolves them.

Known and accepted warnings live in `scripts/testworld-known-warnings.txt` (one regex per line). The script ignores them, so any line it prints is new. Remove a line from that file when the underlying issue is fixed.

Last clean baseline: blocks=140/140, entities=75/75, itemChests=11, items=274, no ERROR lines.

## Shaders in the dev environment

`-PbhShaders` adds Rubidium 0.6.5 and Oculus 1.6.4 (the same versions as the regular client) to the dev runtime through CurseMaven. File ids live in `gradle.properties` (`rubidium_file_id`, `oculus_file_id`).
- `make client-shaders` runs the dev client with them. `make testworld-shaders` runs the automated test world with them.
- Both first run `make sync-shaders`, which copies `SHADER_PACKS` from your regular `.minecraft/shaderpacks` into `run/shaderpacks` (default: Complementary Reimagined, Complementary Unbound, BSL). Override with `make SHADER_PACKS="a.zip b.zip" ...`.
- Pick the pack in the in-game shader menu. BSL is the strictest, Complementary Reimagined the most mod friendly, Unbound is close to BSL.
- `make shader-cycle` runs the test world and, once built, switches at runtime through every pack in `run/shaderpacks` and finally back to no shaders (Oculus `save()` + `reload()`, the same calls as its menu). It walks the player to the start of the blocks, entities and chests sections for each pack. A crash on a switch ends the run and the script prints the crash report path; `[BHTEST] shader cycle finished ... failures=N` reports packs that did not load. Oculus is resolved by reflection, so the mod does not depend on it.
- Without `-PbhShaders` the dev runtime has no shader mods, so the plain run stays vanilla rendering.

Test-only code (`/bhtest`, the auto test, the shader cycle and `GlStateProbe`) lives in the `devtools` source set (`src/devtools/java`). It is added to the dev run configurations only and never to the release jar. The probe is installed through `DeferredRenderRegistry.setObserver` when `-PbhGlProbe` is set. After adding a Gradle source set, refresh the Gradle project in the IDE.

Commit messages are checked by `scripts/git-hooks/commit-msg`, which rejects Claude co-authorship and generation lines. On a fresh clone run `git config core.hooksPath scripts/git-hooks` once.

## GameTests

`make gametest` (`./gradlew runGameTestServer`) starts a dedicated server, builds the tests in `src/devtools/java/.../devtools/gametest` and prints `All N required tests passed` in `run/logs/latest.log`. A failure exits with code 1. The tests live in the `devtools` source set, so they are never in the release jar, and they run on a dedicated server on purpose: if the mod loads a client class on the server, this run crashes at startup (it already found this kind of bug, see ADR 0017).
- `MachineGameTests`: both condensers and the infusor make every recipe they have; one test per registered block places it, ticks it for 30 ticks and checks that its block entity is still there; one test per registered entity spawns it and lets it tick for 30 ticks (535 tests today). They catch a crash on a dedicated server for anything reachable from the registries, the block entities and the entities. They do not run item use, packet handlers or events, so a client class hidden there can still get through.
- `ItemGameTests`: one test per registered item uses it in the air and on a block with a mock player on the dedicated server and lets it run 10 ticks. It found a regression in `ClientItemHooks` (a method that returned `LocalPlayer` as `Entity` made the whole class unloadable on the server).
- `AltarGameTests`: every recipe of the blood altar (4 offerings, corrupted blood flask) and of the blasphemous altar (3 offerings repeated on the 4 pedestals, blood flask) is performed with a mock player: the altar activates, the ritual consumes the offerings and delivers the result (the player takes it from the blood altar; the tentacle drops it for the blasphemous one). A generator makes one test per recipe index up to `RITUAL_SLOTS` (32); an index past the number of recipes passes by itself and a test fails when a type has more recipes than slots (19 blood and 13 blasphemous today). Two tests check that offerings that match no recipe are not consumed.
- `FluidMachineGameTests`: the harvester turns each soul type into its fluid and feeds an adjacent tank; the sanguinite tank rejects corrupted blood and the rhnull tank takes every fluid; both pipes move fluid into a tank; the sanguinite pipe bursts into a corrupted blood block when it holds corrupted blood or viscous blasphemy and the rhnull pipe does not; the sanguinite condenser and tank refuse both fluids and the rhnull ones accept them.
- `FluidChainGameTests` (helper `FluidChain` lays machines in a row and connects the pipes): a tank feeds a condenser through a pull pipe and a push pipe and the gem comes out (ancient, great ancient, ancient blasphemous, great ancient blasphemous); the output tank of the Unknown Portal reaches a rhnull condenser through pipes and a tank; the harvester blood reaches the portal and makes it progress; two harvesters (blood and visceral blood) feed the infusor through two pairs of pipes and a sanguinite ingot comes out as rhnull; a sanguinite pipe pulling viscous blasphemy from the portal bursts; edge cases of the condenser (no frame, too little fluid, leftover fluid, a stack of frames, a wrong fluid, a sanguinite condenser behind a rhnull chain). A chain needs two pipes between machines, one in pull mode and one in push mode, see items 44 and 45 of `docs/TECH_DEBT.md`.
- `PortalGameTests`: the Chalice of the Damned lights a frame of sanguinite blocks when it is used on the bottom frame block, and does nothing without a frame.
- `SproutGameTests`: the Blood Gem Sprout takes the blood of a filled flask.
- `SpellCastGameTests`: every spell book is cast with a Forge `FakePlayer` (a real `ServerPlayer`; the mock player cannot be cast to it) with a fresh player per test and a check that `use()` starts the cast, through the Scarlet Cinder staff with the book in the off hand, through the staff with the Omnitome in the off hand, through the Omnitome and the loose book, each in the main hand and in the off hand. The Crimson Veil spent is compared with the expected number of casts: sequential books fire once per charge plus cooldown cycle, thrown ones once (by the staff while charging, by the Omnitome on release) and the others once on release (66 tests, ADR 0018).
- `OffspringGameTests`: an Offspring of the Unknown under 30% health moves at least four blocks away from its target, and a healthy one in the same setup does not (the control that shows the first test can fail).
- `BlockDropGameTests`: one test per block runs `BlockAudit.violations` on the dedicated server: the block has a rule in `BlockToolRules`, its tags match the rule, it either has a loot table or is on the short list of blocks that drop nothing on purpose (fire, fluids, the portal, the gem sprout, the Oni statue, the tomb block, the infested vein), the minimum tool of its tier makes it drop, a tool one level below or the bare hand does not, its hardness is neither negative nor above 50 (fluids, the portal and the Seliora block are exempt) and the minimum tool of its tier mines it in at most 20 seconds (today the slowest block takes 2.3 seconds). The test world runs the same rules.
- `EntityDropGameTests`: one test per living entity of the mod. It has a loot table and a simulated kill by a player (300 rolls) drops at least one real item, or it is on one of the two short lists of entities that drop nothing: the ones that drop nothing by design (the two souls, the dagger clone) and the ones whose drops are not designed yet (an empty or missing loot table). A listed entity that does drop makes the test fail so the list does not go stale. It writes `run/bh-entity-drops.txt` with what each creature dropped in the simulated kills.
- `ProgressionGameTests`: the progression guard. On the running game it works out which items of the mod can be obtained: vanilla items; what every block really drops with every tool (hand, shears, silk touch and the tool chain, a mod tool only once it is obtainable) in all its block states; what the mobs that can spawn drop (a biome spawn rule or a mob declared with its structure) when a player kills them; the chests; every recipe of every type once all its ingredients are obtainable; and the sources that only the code gives, declared in `src/devtools/resources/progression-sources.txt`. An item that is none of those fails the test. The report is written to `run/bh-progression.txt` with the source of each item and `[NEEDS MOB DROPS]` or `[NEEDS A CHEST]` when it cannot be obtained without them, and `run/bh-mob-sources.txt` says which entities can spawn.
- `ProgressionRootsGameTests` proves the sources that only the code gives: an empty flask takes blood, corrupted blood and viscous blasphemy from a tank; the seed places a sprout on blood dirt and the sprout, fed with sanguinite, lapis, a gold nugget or an amethyst shard, grows and drops the pure, aventurine, citrine or tanzarine blood gem; breaking an ancient blood capsule eventually drops the unknown entity finger; the sacrificial dagger turns an empty flask into a bound blood flask.
- `ProgressionSourcesTest` (static): every line of `progression-sources.txt` names a registered item, every `code` source names a class that really mentions the item, and every `mob` source names a structure that has a definition and a structure set and a template that contains the entity.
- `RegistryDumpGameTests`: writes `run/bh-registry-ids.txt`, one line per id the mod registers (items, blocks, entities, fluids, effects, sounds, worldgen, tags, recipes). It is the list that modpacks depend on; compare it with the dump of the previous release to see what a version removes or renames.
- `GeckoAssetGameTests`: every item with a `getId()` (items, staffs, spell books, combo weapons and armor) has the geo, texture and animation file that the generic model builds from it; armor looks without animation skip the animation file. The static test `GeckoBaseRulesTest` fails when a class implements `GeoItem` or `GeoBlockEntity` outside the bases (rule in `docs/CONTRIBUTING.md`).
- `CrimsonVeilHudGameTests`: the Crimson Veil HUD rule (`CrimsonVeilHelper.isHoldingConsumer`, the same one the overlay uses) is true for every spell book and the Omnitome in either hand, for every staff in the main hand and for a staff with a book, and false with empty hands or a pickaxe and a shield (29 tests). The consumer tag is filled from the registry by the data generator, so a new book is never forgotten.
- `StaffBonusGameTests` (one batch per test, so the targets of one test are never counted by its neighbour): the Scarlet Cinder counts the targets with Blood Fire and the Incomplete Staff the ones with Bleeding, up to five; a target with another effect or out of range does not count; a dying target does not count; the count goes back to zero when the staff is not selected and is refreshed when a cast starts; an affine book gets the full bonus (damage, Crimson Veil cost) and one of another affinity 40% of it; the Daggers of the Rain get the damage bonus; a sequential book fires faster with a charged staff (10 tests, ADR 0018).
- Not covered yet: sprout growth and gems, the Sanguine Lapidary, the Rhnull blood engine modes (its design is still open), the Unknown Portal machine output, the Voracious Mushroom, the portal teleport itself.
- Optional tests (`required = false`) state a design intent that the code does not meet yet. They do not fail the run; they appear as `(optional) ... failed` in the log. Today: `exactlyTheRecipeAmountCrossesTwoPipesIntoTheCondenser` and `pushPipeNextToTheHarvesterDoesNotSendTheBloodBack` (items 44 and 45 of `docs/TECH_DEBT.md`). Remove `required = false` when the item is fixed.
- Every test uses the template `bloodyhell:empty` (`src/devtools/resources/data/bloodyhell/structures/empty.nbt`, a 9x6x9 smooth stone floor). It is generated with `python scripts/generate-gametest-structure.py` and can be regenerated at any time.
- A new test is a static method with `@GameTest(template = "empty", batch = "...")` in a class annotated `@GameTestHolder(BloodyHell.MODID)` and `@PrefixGameTestTemplate(false)`; `@GameTestGenerator` makes one test per element of a registry.

`make check` runs `test`, `gametest` and `testworld` in a row: run it before a push.

## Static resource test

`make test` (`./gradlew test`) runs `ResourceIntegrityTest`, plain JUnit with no Minecraft classes. It reads the Java sources and the resources (`src/main` and `src/generated`) and fails when:
- a registered block has no blockstate or a registered item has no model;
- a block or item has no lang entry, or a lang key is duplicated;
- a model parent or texture, a blockstate model, a particle texture, a shader source or a sound file does not exist;
- Java code points to a missing mod resource through `MODID, "textures/..."` style literals;
- a loot table or recipe references an item that is not registered;
- a registered block is missing from `BlockToolRules`;
- a registered entity has no lang entry or no renderer, or a living entity has no attributes registered;
- a registered sound event is not in `sounds.json`, a `sounds.json` entry has no event, or a subtitle has no lang entry (subtitle keys are `subtitles.bloodyhell.<sound id>`);
- a registered particle has no provider in `ClientEvents`, or a sprite particle has no `particles/<id>.json`.

The test world also runs a harvest matrix (`BlockAudit.checkHarvest`) on every block with a loot table, using the real tags, tiers and loot tables of the running game: with the minimum tool of the rule the block must drop something; with a tool of the same kind one level below, or with the bare hand, it must drop nothing when the rule has a tier; and a block with no tool must drop something with the hand, shears or silk touch. Blocks whose drops are random (leaves) or that cannot be harvested on purpose are exempt in `HARVEST_EXEMPT`. A failure is logged as `[BHTEST] block audit violation`.

The test world adds the runtime half of the block check: `BlockAudit` logs `[BHTEST] block audit violation` (an ERROR line, so the script fails) when the resolved tags or the tool requirement of a block disagree with its rule in `BlockToolRules`.

Registered names come from `ITEMS.register`, `BLOCKS.register`, `registerBlock` and `registerBlockWithoutBlockItem`, so a new registration helper needs a new pattern in the test. Accepted gaps go in `src/test/resources/known-resource-gaps.txt` (the exact failure line, with a `#` comment saying why); the test run prints entries that no longer fail.

## Render refactor safety net

Two checks that need no mixins, used to prove that a refactor of the render managers did not change the OpenGL state calls.
- `scripts/check-gl-calls.ps1` compares, per manager source file, the ordered list of `RenderSystem` / `GlStateManager` / `GL*` / uniform lines against `scripts/gl-baseline/calls/`. The matrix capture and restore lines are ignored, because the base class takes them over. `-Snapshot` rewrites the baseline, only do that after a deliberate behavior change. Exit code 2 means a difference.
- `GlStateProbe` (enabled with `-PbhGlProbe`, or `scripts/run-testworld.ps1 -GlProbe`) reads the real GL state with `glGet` before and after every manager in `ModRenderEvents` and logs each distinct transition as `[GLPROBE]`. `scripts/glprobe.ps1 -Name vanilla -Save` stores the transitions of the last run as a baseline, `scripts/glprobe.ps1 -Name vanilla` compares the last run against it: a transition that is not in the baseline fails, a baseline transition that was not seen only reports scene coverage. `-Merge` adds the transitions of the last run to the baseline, use it when a legitimate new case shows up. Take one baseline without shaders and one with a shader pack (`-Shaders`, then select the pack), always on the unmodified code.
- The probe only sees managers that had queued work during the run, and it checks the state at the boundary of each manager (leaks and wrong restores), not the calls in the middle. The static check covers the middle.

## Manual checklist (needs a person)
What the automatic checks cannot see. Tick an item only after looking at it in game; untick it when a change touches that area.

Plants and drops
- [ ] Blood Lily: placed on water it floats like a lily pad and emits light; breaking it drops the lily itself.
- [ ] Spiky grass and rounded grass: shears drop the grass, the bare hand drops nothing.
- [ ] Blood grass: shears drop the grass, the bare hand sometimes drops an eyeball seed.
- [ ] Small rocks: a pickaxe drops them; the hand does not.

Tools and tiers (drops and tiers of every block are checked by the harvest matrix of the test world)
- [ ] Tool and armor feel: sanguinite, blasphemite and rhnull are in that order; blasphemite and rhnull armor no longer make you immune to knockback.

Magic
- [x] Staff tooltips and the spell books loose, with a staff and with the Omnitome (checked by the author on 2026-10-23, not every case).
- [ ] The new recipes of the Omnitome and the three staffs are reachable and their difficulty feels right.

World and systems
- [ ] The Chalice of the Damned lights a portal frame; the portal teleports both ways.
- [ ] Viscous blasphemy comes out of the Unknown Portal machine and fills a flask.
- [ ] Subtitles on: veinraver, seliora, offspring and the machines show readable names, never a key like `subtitles.bloodyhell.x`.

Visuals
- [ ] Item displays calibrated by Mitemi in the GUI: altars, engine, pillar, condensers, pipes, harvester, portal.
- [ ] The Seliora resting block item in the hand and in the GUI (the display is a copy of the portal one and needs calibration).
- [ ] Omnitome closed and open, in the hand and in the GUI: the closed cover hides the pages.
- [ ] The three armor sets (sanguinite, rhnull, blasphemite) and the three combo weapons (Impaler, Hulking Mass of Iron, Twin Daggers) look as before in hand, on the ground and worn: their model and renderer now come from the generic ones (rhnull and blasphemite armor keep the glow, blasphemite armor has no animation).
- [ ] Staff orbs on the ground; frenzied trails do not show through walls. The three staffs, in hand and in the GUI, keep their own model and orb (their renderer now comes from `StaffRenderers`).
- [ ] No jump of the shader effects after 100 seconds of play.

Compatibility
- [ ] Load a world saved with 0.8.585 (`build/libs/bloodyhell-0.8.585.jar` is the old jar): it loads, the game logs the removed items and blocks, and Forge makes a backup of the world.
- [ ] Two separate launches with a new world each: one without Rubidium and Oculus, one with both and a shader pack (BSL and Complementary).
- [ ] A dedicated server starts and a client joins it.
- [x] Walk the entity grid and check every model, texture and animation looks right (with and without a shader pack).

## Modpack compatibility
Registry ids are the public face of the mod: modpack scripts, quests and datapacks name them. After `./gradlew runGameTestServer` (it writes `run/bh-registry-ids.txt`), check a pack with `python scripts/check-modpack-ids.py --pack <pack folder>`. It lists the `bloodyhell:` ids that the files of the pack and the data inside its other mods name and the mod does not register, and exits with 1 when there are any. ADR 0019 explains what happens to an existing world when an id disappears and why ids are not renamed.

