# 0001. Deferred rendering for shader pack compatibility

Status: Accepted

## Context
Drawing custom shader effects immediately breaks or makes them disappear under shader packs (Iris/Oculus), which redirect the vanilla pipeline.

## Decision
Renderers, particles and item renderers only queue data into a `*RenderManager` (`util/visuals/manager`). The managers draw everything at `RenderLevelStageEvent.Stage.AFTER_LEVEL`, called from `ModRenderEvents`.

## Consequences
New custom rendering must submit to a manager instead of drawing immediately. See 0002, 0003 and 0004 for the pitfalls this introduces.
