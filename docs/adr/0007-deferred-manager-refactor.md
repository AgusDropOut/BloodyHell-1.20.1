# 0007. Deferred manager refactor keeps the GL state calls untouched

Status: Proposed

## Context
Around 26 managers repeat the queue, the shadow pass guard and the matrix capture, and each one carries OpenGL state calls that were tuned by trial and error. Minecraft will move to Vulkan, and the mod may be ported to other loaders.

## Decision
- Move only the identical parts (queue, guard, matrix capture and restore, ordered registry) into a `DeferredRenderManager` interface and a base class. The state calls stay verbatim, in the same order, inside each manager.
- Migrate one manager per commit and verify with `scripts/check-gl-calls.ps1` and `GlStateProbe` (see `docs/TESTING.md`).
- Behavior changes (items 1 to 6 of `docs/TECH_DEBT.md`, shared scene color copy, shared shader time) are separate commits after the refactor.
- Keep the render package free of `net.minecraftforge` imports, so only a thin event adapter is loader specific.
- Later, describe each effect state declaratively, which maps to pipeline objects, and isolate the main target copy in one place.

## Consequences
The call order in `ModRenderEvents` stays the registry order (see 0002).
