# 0004. Capture projection and modelview matrices when queueing

Status: Accepted

## Context
At `AFTER_LEVEL` the global matrices no longer match the ones active when the object was submitted. The first-person hand uses a different projection than the world, which caused misplaced or distorted effects.

## Decision
Copy `RenderSystem.getProjectionMatrix()` and `getModelViewMatrix()` when queueing (per task, or on the first element for `BaseShaderRenderManager`) and restore them while drawing. Restore the previous projection and vertex sorting afterwards.

## Consequences
Queued data holds matrix copies, not references.
