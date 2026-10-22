# Technical Debt

Known issues found during the review of the render managers. Not decisions: remove each item once fixed.

1. Shader time wraps every 100 s (`% 100000`), causing a visible jump. Time comes from three sources (`Util.getMillis`, `System.currentTimeMillis` in `MiniBlobRenderManager` and `BloodStainDecalRenderManager`, and particle age) and the formula is duplicated in about 10 places.
2. `BaseShaderRenderManager` and the orb managers restore the projection with `VertexSorting.ORTHOGRAPHIC_Z` instead of the previous sorting (the Omnitome managers do it correctly).
3. Five managers (Blob, Orb, MiniBlob, GoldenSorrow, ScarletCinder) each copy the full main target every frame into their own texture, and never release it on shutdown.
4. `renderAll(event)` managers re-check `AFTER_LEVEL` although `ModRenderEvents` already filters the stage.
5. Managers are invoked in three different styles: static `renderAllAndClear()`, `INSTANCE.renderAllAndClear()` and `renderAll(event)`.
6. `OmnitomeRenderer.isManagerRendering` and `isChildMorphing` are static mutable flags that are not reset if rendering throws.

Refactor ideas: one `DeferredRenderManager` interface with an explicit ordered registry, a shared scene-color copy, and a single `ShaderTime` helper.

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
17. `InsightRenderManager` leaves the depth test disabled after drawing (seen by `GlStateProbe`), and with shaders active some manager before `BloodDrop` leaves `depthMask(false)` with a changed blend func. Later managers restore them by accident. Preserved on purpose by the render refactor, fix as a separate behavior change.
