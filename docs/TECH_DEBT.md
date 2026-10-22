# Technical Debt

Known issues found during the review of the render managers. Not decisions: remove each item once fixed.

1. Shader time wraps every 100 s (`WRAP_MILLIS` in `ShaderTime`), causing a visible jump. The formula now lives in one place; particle age animations are still separate.
3. `SceneColorCopy` never releases its texture on shutdown (the context goes away with the game, harmless in practice).

Refactor ideas: one `DeferredRenderManager` interface with an explicit ordered registry (done), a shared scene-color copy (done) and a single `ShaderTime` helper (done).

## Found by the test world

7. `seliora_resting_block` blockstate references `bloodyhell:block/seliora_resting_block`, which does not exist. Ignored by `scripts/testworld-known-warnings.txt` until fixed.
8. Nine block items have no `#inventory` model: `ancient_torch_block`, `blood_gem_sprout`, `blooddirt_farmland`, `crimson_lure_mushroom_block`, `giant_blood_sapling`, `rhnull_condenser`, `sanguinite_condenser`, `seliora_resting_block`, `small_blood_sapling`.
9. Blockstates with missing variant models: `star_lamp_block`, `giant_blood_sapling`, `small_blood_sapling` (the fluid blocks are probably harmless).
10. `sounds/dagger_attack_3.ogg` is declared but the file does not exist.
11. GeckoLib rejects the geo version of `the_omnitome`, `staff_of_scarlet_cinder`, `staff_of_the_golden_sorrow` and `incomplete_staff_of_the_pure_blooded`.
12. Animated fluid textures `blood_fluid_flowing_texture` and `rhnull_blood_fluid_flowing_texture` have frame indexes out of range in their `.mcmeta`.
13. 28 "Unknown recipe category" warnings in the client recipe book (likely mod recipes without a category).
14. Several shaders declare samplers or uniforms that do not exist in the program (`insight_distortion`, `omnitome_void`, `shape_unknown_fire`, `blood_blob`, `ethereal_swirl`, `frenzy_tentacles`, `scarlet_cinder_orb`, `spell_wheel_bg`). Possibly optimized out by the GLSL compiler, to be confirmed.
15. JEI reports the creative tabs "Blood Tab" and "Operator Utilities" without display items.
16. Entities that depend on a `target` field (`UnknownEntityArms`, `SanguineSacrificeEntity`) do not save it to NBT, so they are discarded on world reload. Fixed the crash, the persistence is still missing.

Fixed with the test world: null `target` crashes in `UnknownEntityArms` and `SanguineSacrificeEntity`; `CyclopsEntity` and `BlasphemousTwinDaggersCloneEntity` attributes were never registered.
17. The Insight task queue (`DeferredTaskQueues.INSIGHT`) leaves the depth test disabled after drawing (seen by `GlStateProbe`), and with shaders active some manager before `BloodDrop` leaves `depthMask(false)` with a changed blend func. Later managers restore them by accident. Preserved on purpose by the render refactor, fix as a separate behavior change.
18. `glowing_crystal_glass_block` and `ancient_blood_capsule` render with the sky color under Rubidium 0.6.5 and Oculus 1.6.4 without shaders, because they need the translucent chunk layer (see ADR 0008). Retest when the pinned Rubidium or Oculus version changes. Fallback: flatten their alpha and use `cutout`.
20. The frenzied trail (`Effects.FRENZIED_TRAIL`) draws with `LINEAR_FRENZIED_FLAME_SHADER` but sets `AnimTime` on `RADIANT_ENERGY_SHADER`, so the shader it draws with never receives the time. Preserved when the managers were merged into `TrailRenderManager` (the `timeTarget` of the effect). Decide whether it should animate and then pass the same shader for both.
21. `OrbRenderManager` restores the event projection with a hardcoded `VertexSorting.ORTHOGRAPHIC_Z` instead of the previous sorting (the fix of the old item 2 only reached `DeferredMatrices` and the Omnitome managers). Behavior change, own commit.
28. The Omnitome managers and `TaskQueueRenderManager` solve the same problem (run vanilla or GeckoLib drawing at the end of the pipeline) with different shapes: structured tasks with a `MatrixSnapshot` per task against `Runnable` tasks with one capture. See the table of direct managers in `docs/RENDER_ARCHITECTURE.md`.
29. The Omnitome has no void pages in the GUI. `OmnitomeRenderer.renderRecursively` does not draw the page bones, only `OmnitomeVoidLayer` does, and it queues them in `OmnitomeVoidRenderManager`, which draws at `AFTER_LEVEL`. GUI item rendering happens after that stage, so the void pages are drawn in the next frame, before the GUI is drawn over them. In the hand the book is replayed inside `AFTER_LEVEL` by `OmnitomeBookRenderManager` and the void manager runs after it in the same pass, which is why it works there. A fix is to draw the pages immediately in the layer when the context is the GUI, with `bufferSource.getBuffer(ModRenderTypes.getOmnitomeVoid(texture))`. Not applied: it is not required and needs a look at the inventory and hotbar.
