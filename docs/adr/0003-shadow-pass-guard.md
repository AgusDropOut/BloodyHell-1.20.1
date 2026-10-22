# 0003. Skip deferred queueing during the shadow pass

Status: Accepted

## Context
With shader packs, the shadow pass invokes our item, entity and particle renderers a second time, which queued duplicate deferred draws. Managers capture the matrices on the first queued task, so a task queued by the shadow pass made the whole queue draw with the orthographic shadow projection.

## Decision
`RenderPassGuard.shouldSkipDeferred(transform)` is an early exit at the top of every `queue*`/`add*` method of the managers, whether driven by items, entities or particles. The no-argument overload covers entities and particles. It detects the shadow pass through the Iris API (resolved by reflection, both the `net.irisshaders` and `net.coderbot` package names) and, independently, through an orthographic projection (`projection.m33 ~= 1`) outside the GUI context.

## Consequences
Every new manager must call the guard when queueing. `MiniBlobRenderManager` and the `BaseShaderRenderManager` blob managers are not guarded yet.
