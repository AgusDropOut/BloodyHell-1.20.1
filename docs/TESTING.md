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

Last clean baseline: blocks=141/141, entities=75/75, itemChests=11, items=284, no ERROR lines.

## Shaders in the dev environment

`-PbhShaders` adds Rubidium 0.6.5 and Oculus 1.6.4 (the same versions as the regular client) to the dev runtime through CurseMaven. File ids live in `gradle.properties` (`rubidium_file_id`, `oculus_file_id`).
- `make client-shaders` runs the dev client with them. `make testworld-shaders` runs the automated test world with them.
- Both first run `make sync-shaders`, which copies `SHADER_PACKS` from your regular `.minecraft/shaderpacks` into `run/shaderpacks` (default: Complementary Reimagined, Complementary Unbound, BSL). Override with `make SHADER_PACKS="a.zip b.zip" ...`.
- Pick the pack in the in-game shader menu. BSL is the strictest, Complementary Reimagined the most mod friendly, Unbound is close to BSL.
- `make shader-cycle` runs the test world and, once built, switches at runtime through every pack in `run/shaderpacks` and finally back to no shaders (Oculus `save()` + `reload()`, the same calls as its menu). It walks the player to the start of the blocks, entities and chests sections for each pack. A crash on a switch ends the run and the script prints the crash report path; `[BHTEST] shader cycle finished ... failures=N` reports packs that did not load. Oculus is resolved by reflection, so the mod does not depend on it.
- Without `-PbhShaders` the dev runtime has no shader mods, so the plain run stays vanilla rendering.

Test-only code (`/bhtest`, the auto test, the shader cycle and `GlStateProbe`) lives in the `devtools` source set (`src/devtools/java`). It is added to the dev run configurations only and never to the release jar. The probe is installed through `DeferredRenderRegistry.setObserver` when `-PbhGlProbe` is set. After adding a Gradle source set, refresh the Gradle project in the IDE.

Commit messages are checked by `scripts/git-hooks/commit-msg`, which rejects Claude co-authorship and generation lines. On a fresh clone run `git config core.hooksPath scripts/git-hooks` once.

## Static resource test

`make test` (`./gradlew test`) runs `ResourceIntegrityTest`, plain JUnit with no Minecraft classes. It reads the Java sources and the resources (`src/main` and `src/generated`) and fails when:
- a registered block has no blockstate or a registered item has no model;
- a block or item has no lang entry, or a lang key is duplicated;
- a model parent or texture, a blockstate model, a particle texture, a shader source or a sound file does not exist;
- Java code points to a missing mod resource through `MODID, "textures/..."` style literals;
- a loot table or recipe references an item that is not registered;
- a registered block is missing from `BlockToolRules`.

The test world adds the runtime half of the block check: `BlockAudit` logs `[BHTEST] block audit violation` (an ERROR line, so the script fails) when the resolved tags or the tool requirement of a block disagree with its rule in `BlockToolRules`.

Registered names come from `ITEMS.register`, `BLOCKS.register`, `registerBlock` and `registerBlockWithoutBlockItem`, so a new registration helper needs a new pattern in the test. Accepted gaps go in `src/test/resources/known-resource-gaps.txt` (the exact failure line, with a `#` comment saying why); the test run prints entries that no longer fail.

## Render refactor safety net

Two checks that need no mixins, used to prove that a refactor of the render managers did not change the OpenGL state calls.
- `scripts/check-gl-calls.ps1` compares, per manager source file, the ordered list of `RenderSystem` / `GlStateManager` / `GL*` / uniform lines against `scripts/gl-baseline/calls/`. The matrix capture and restore lines are ignored, because the base class takes them over. `-Snapshot` rewrites the baseline, only do that after a deliberate behavior change. Exit code 2 means a difference.
- `GlStateProbe` (enabled with `-PbhGlProbe`, or `scripts/run-testworld.ps1 -GlProbe`) reads the real GL state with `glGet` before and after every manager in `ModRenderEvents` and logs each distinct transition as `[GLPROBE]`. `scripts/glprobe.ps1 -Name vanilla -Save` stores the transitions of the last run as a baseline, `scripts/glprobe.ps1 -Name vanilla` compares the last run against it: a transition that is not in the baseline fails, a baseline transition that was not seen only reports scene coverage. `-Merge` adds the transitions of the last run to the baseline, use it when a legitimate new case shows up. Take one baseline without shaders and one with a shader pack (`-Shaders`, then select the pack), always on the unmodified code.
- The probe only sees managers that had queued work during the run, and it checks the state at the boundary of each manager (leaks and wrong restores), not the calls in the middle. The static check covers the middle.

## Manual checklist (not automatable)
- [ ] Omnitome in first person and in the GUI.
- [ ] Raymarching: blood blob, blood/golden/scarlet orbs.
- [ ] Blood stain decals.
- [ ] Shadow pass with Iris (no duplicated draws).
- [ ] Translucent blocks (glowing crystal glass, ancient blood capsule) with Oculus and shaders disabled: known to look wrong, see ADR 0008.
- [x] Walk the entity grid and check every model, texture and animation looks right (with and without a shader pack).
