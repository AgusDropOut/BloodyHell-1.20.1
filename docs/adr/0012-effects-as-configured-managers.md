# 0012. Effects are configured instances of a few generic managers

Status: Accepted

## Context
Twenty-six deferred managers existed, most of them copies of each other: three orbs, six billboards, two trails. Each was a static singleton called by its own name from renderers and particles, with its data class nested inside. Adding an effect meant copying a manager, and moving to another loader or rendering API meant touching every call site.

## Decision
- Every deferred manager is an instance of `DeferredRenderManager`. The registry lists instances only, in the order of ADR 0002, and `FrameContext` replaces the Forge stage event.
- Effects that differ only in configuration share one manager class: `OrbRenderManager` (`OrbEffect`), `BillboardRenderManager` (`BillboardEffect`) and `TrailRenderManager` (`TrailEffect`). The descriptors live in `client/render/effect/api`, their data records in `effect/data`, and the OpenGL state of an effect is a pair of `Runnable` in `EffectStates`, so the calls stay verbatim and in the same order (ADR 0007).
- `QueuedRenderManager<T>` (`deferred/base`) holds the skeleton every queued effect repeated: the shadow pass guard, the matrix capture, the `EffectState` begin, applying the view, drawing the queue, restoring and the `EffectState` end. A subclass only supplies the draw (and, for the blood drop, how the view is applied). The billboard, trail, task queue, black hole entity, radial distortion, magic ring and noise sphere managers extend it, and the spherical shield is a task queue with its own state. `EffectState` lives in `deferred/api` because it is not specific to effects.
- `Effects` is the single place that creates every effect instance. Renderers, particles and item renderers submit through `Effects.X` and never name a manager class. Effects with their own geometry (black holes, magic ring, noise sphere, shield, blobs, decal) are instances there too.
- Contracts and data are kept out of the packages that hold implementations: `deferred/api`, `effect/api`, `effect/data`.
- The effect and deferred packages import nothing from the particle or block code. A manager receives a callback (`ScreenSampledDraw` for the black hole) or reads a neutral registry that the owner fills (`BloodBlobEmitters`, filled by the engine block entity). The Omnitome managers receive the draw as a callback (`ItemDraw`, `VoidDraw`) and the child renderer as a parameter, and take the per-task matrices through `MatrixSnapshot`. The pass flags the item renderers read live in `ItemPassState` and are reset in a `finally`. No manager imports Forge.

## Consequences
A new effect of an existing family is one line in `Effects`. A different rendering API or loader replaces the managers and the event adapter, not the call sites.
The shared draw loop forces the per-item or batched time uniform to be a descriptor option, and the frenzied trail keeps its `timeTarget` because it sets the time on another shader than the one it draws with (TECH_DEBT item 20).
