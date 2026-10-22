# Technical Debt

Known issues found during the review of the render managers. Not decisions: remove each item once fixed.

3. `SceneColorCopy` never releases its texture on shutdown (the context goes away with the game, harmless in practice).

Refactor ideas: one `DeferredRenderManager` interface with an explicit ordered registry (done), a shared scene-color copy (done) and a single `ShaderTime` helper (done).

## Found by the test world

8. Three blocks still have an automatic `BlockItem` without an `#inventory` model: `blooddirt_farmland` and `seliora_resting_block` (their loot tables drop themselves, so the item is real but invisible in the inventory) and `crimson_lure_mushroom_block` (silk touch drops it). Give them item models in `ModItemModelProvider`, or point their drops at a real item. The other eight blocks that had a dead automatic item now use `registerBlockWithoutBlockItem`.

Fixed with the test world: null `target` crashes in `UnknownEntityArms` and `SanguineSacrificeEntity`; `CyclopsEntity` and `BlasphemousTwinDaggersCloneEntity` attributes were never registered.
18. Known limitation, not ours: `glowing_crystal_glass_block` and `ancient_blood_capsule` render with the sky color under Rubidium 0.6.5 and Oculus 1.6.4 without shaders, because they need the translucent chunk layer and Rubidium draws it wrong (see ADR 0008). Retest when the pinned Rubidium or Oculus version changes. Fallback if it ever matters: flatten their alpha and use `cutout`.
28. The Omnitome managers and `TaskQueueRenderManager` solve the same problem (run vanilla or GeckoLib drawing at the end of the pipeline) with different shapes: structured tasks with a `MatrixSnapshot` per task against `Runnable` tasks with one capture. See the table of direct managers in `docs/RENDER_ARCHITECTURE.md`.
30. The staff orbs are not drawn in the GUI (`StaffOrbLayer` skips the GUI context on purpose). The orb shaders raymarch in perspective view space around the orb centre, and in the GUI the pose is in pixel space with an orthographic projection, so the quad and the sphere would not fit. Drawing them there would need an immediate path with its own sphere shader, like the Omnitome void pages, which the void layer now draws immediately in the GUI.
31. Accepted as is: `small_blood_sapling` and `giant_blood_sapling` use the `blood_sapling` texture. They had no art of their own and rendered as the missing texture cube. Give them their own textures if that ever matters.
32. GeckoLib 4.8.2 only knows geometry format 1.12.0 and warns for any other version, but it still builds the model. Blockbench exported `the_omnitome` and the three staffs as 1.21.110; their `format_version` was set to 1.12.0 by hand (they use no rotated face UVs, which is what the newer formats add). Export the Bedrock Entity models as 1.12.0, or the warning comes back. Decided not to re-export the existing models one by one; do it when a model is touched anyway or at the next mod version bump.
