# 0009. One scene color copy per deferred pass and one shader clock

Status: Accepted

## Context
Each of the five raymarching managers (blood blob, mini blob, three orbs) kept its own texture and copied the full main target every frame it had work, so up to five full-screen copies per frame and five textures. The animation time formula was repeated in 22 places, mixing `Util.getMillis` and `System.currentTimeMillis`. ADR 0005 and ADR 0006 recorded both as consequences.

## Decision
- `SceneColorCopy.acquire(mainTarget)` owns a single texture, resizes it with the window and copies the main target only the first time it is requested in a pass. `DeferredRenderRegistry.renderAll` invalidates it at the start of every pass. Managers still bind it on every request.
- `ShaderTime.seconds()` is the only source of shader animation time. It keeps the 100 second wrap that the shaders were tuned with.

## Consequences
A raymarcher that runs later in the same pass samples the scene as it was before the first raymarcher drew, so it does not refract the output of an earlier one. This is the price of saving the copies. If it ever looks wrong, invalidate the copy after a manager draws, at the cost of copying again.
The wrap jump (TECH_DEBT item 1) can now be changed in one place. The shared texture is never released on shutdown.
