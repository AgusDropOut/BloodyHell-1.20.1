# 0012. Effects are configured instances of a few generic managers

Status: Accepted

## Context
Twenty-six deferred managers existed, most of them copies of each other: three orbs, six billboards, two trails. Each was a static singleton called by its own name from renderers and particles, with its data class nested inside. Adding an effect meant copying a manager, and moving to another loader or rendering API meant touching every call site.

## Decision
- Every deferred manager is an instance of `DeferredRenderManager`. The registry lists instances only, in the order of ADR 0002, and `FrameContext` replaces the Forge stage event.
- Effects that differ only in configuration share one manager class: `OrbRenderManager` (`OrbEffect`), `BillboardRenderManager` (`BillboardEffect`) and `TrailRenderManager` (`TrailEffect`). The descriptors live in `client/render/effect/api`, their data records in `effect/data`, and the OpenGL state of an effect is a pair of `Runnable` in `EffectStates`, so the calls stay verbatim and in the same order (ADR 0007).
- `Effects` is the single place that creates every effect instance. Renderers, particles and item renderers submit through `Effects.X` and never name a manager class. Effects with their own geometry (black holes, magic ring, noise sphere, shield, blobs, decal) are instances there too.
- Contracts and data are kept out of the packages that hold implementations: `deferred/api`, `effect/api`, `effect/data`.
- The effect and deferred packages import nothing from the particle or block code. A manager receives a callback (`ScreenSampledDraw` for the black hole) or reads a neutral registry that the owner fills (`BloodBlobEmitters`, filled by the engine block entity). The Omnitome managers still depend on the item renderer.

## Consequences
A new effect of an existing family is one line in `Effects`. A different rendering API or loader replaces the managers and the event adapter, not the call sites.
The shared draw loop forces the per-item or batched time uniform to be a descriptor option, and the frenzied trail keeps its `timeTarget` because it sets the time on another shader than the one it draws with (TECH_DEBT item 20).
