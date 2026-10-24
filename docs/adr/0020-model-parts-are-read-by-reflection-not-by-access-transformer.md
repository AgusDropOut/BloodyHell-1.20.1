# 0020. Model parts are read by reflection, not by an access transformer

Status: Accepted

## Context
The Pain Throne effects (`GlobalBoneHandler`) twist and break the parts of any living mob, so they need the head and body parts of `AgeableListModel`. Both methods (`headParts`, `bodyParts`) are `protected`, so the access transformer made them public, one line for the base class and one for each vanilla model that came up (26 lines in the end).

An access transformer does not cascade to subclasses. A vanilla model that overrides the method as `protected` while the base class is public no longer compiles: `attempting to assign weaker access privileges; was public`. ForgeGradle recompiles all the transformed Minecraft sources (`Compile failed in findRecomp`), so every model that was not in the list showed up as an error (`TadpoleModel`, `TurtleModel`, `WolfModel`, one more each day) and the fix was always one more line. It also could never cover the models of other mods.

## Decision
- The two methods are read through `AgeableModelParts`, which finds them once with `ObfuscationReflectionHelper.findMethod` (SRG names `m_5607_` and `m_5608_`) and calls them reflectively. A reflective call dispatches to the override of the concrete model, so it works for any `AgeableListModel`.
- The 26 access transformer lines for these methods were removed.
- The rule for new needs is in `docs/CONTRIBUTING.md`: do not make a method public when subclasses override it.

## Consequences
- The build no longer prints compile errors for vanilla models, and a new vanilla or modded model needs nothing.
- Checked on 2026-10-23 on the running client with the pig, wolf, turtle, tadpole, zombie, player and axolotl models: each returns its parts.
- A reflective call per frame and per twisted mob. It is cached and only runs on mobs under the Pain Throne effect, so the cost is small.
- If Minecraft or Forge renames the two methods in another version, `AgeableModelParts` fails at class load with a clear error instead of a silent change.
