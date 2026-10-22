# 0011. Rebuild the camera view with its effects when a shader pack is active

Status: Accepted

## Context
In vanilla 1.20.1 `GameRenderer.renderLevel` multiplies the projection, the hurt tilt, the view bobbing and the nausea spin into one matrix (the `projectionMatrix` of `RenderLevelStageEvent`), and the camera rotation goes in a separate pose stack. A manager that rebuilds the view from the camera rotation and projects with the event matrix is therefore consistent with the world.
While a shader pack is in use Iris moves those effects out of the projection and into the model view pose stack (`MixinModelViewBobbing`). In `AFTER_LEVEL` that pose stack is not available: Forge dispatches the stage with the local stack that holds the projection, not the one with the camera rotation. Using `event.getPoseStack()` as a view therefore places everything rotated and floating.
The result was decals and blood drops that jittered against the world with the camera bobbing, only with a pack active.

## Decision
`ViewEffects.applyCameraView(poseStack, camera)` is the single place that rebuilds the view. With a shader pack active it first applies the hurt tilt, the view bobbing and the spin effect, repeating the vanilla math in the same order, and then the camera rotation. Without a pack it only applies the rotation. The spin phase needs `GameRenderer.tick`, exposed by the access transformer.
Managers that rebuild the view (blood stain decal, blood drop) call it instead of writing the rotation themselves.

## Consequences
The vanilla math is copied and must be revisited on a Minecraft or Iris update. The camera roll that other mods add through `ComputeCameraAngles` is not covered.
The blood blob and mini blob still use a clean view and `u_BobbingOffset`, which only compensates the bob translation, see `docs/TODO.md`.
