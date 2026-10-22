# 0008. Blocks with binary alpha render in the cutout layer

Status: Accepted

## Context
With Rubidium 0.6.5 and Oculus 1.6.4 and shaders disabled, geometry in the chunk `translucent` layer is drawn with the sky color instead of its texture. The altars, the sanguine crucible (blood workbench) and the mushroom blocks were registered as translucent, so they looked like flat silhouettes in the world while their item models, which do not go through the chunk layers, looked correct. Their textures only contain fully opaque or fully transparent pixels, so the translucent layer was never needed.

## Decision
Blocks whose textures have binary alpha use `cutout`: through `"render_type": "minecraft:cutout"` in the model JSON, or through `ItemBlockRenderTypes.setRenderLayer` in `ModRenderers.registerBlockRenderLayers` when the model has no `render_type`. A `render_type` in the model JSON takes precedence over `setRenderLayer`. Translucent stays only where partial alpha is real.

## Consequences
`glowing_crystal_glass_block` and `ancient_blood_capsule` have partial alpha and still need `translucent`, so they look wrong under Rubidium without shaders. This is confirmed to be a Rubidium issue and nothing in the mod causes it (the mod only sets the shader fog to `Float.MAX_VALUE`, meaning no fog). The workaround, if ever wanted, is to flatten their alpha and move them to `cutout`, which loses the semitransparency. A newer Rubidium may fix it, retest when the pinned versions change.
