# 0003. Skip deferred queueing during the shadow pass

Status: Accepted

## Context
With shader packs, the shadow pass invokes our item renderers a second time, which queued duplicate deferred draws.

## Decision
`RenderPassGuard.shouldSkipDeferred(transform)` is an early exit at the top of every `queue*` method of item-driven managers. It detects the shadow pass through the Iris API (resolved by reflection, both the `net.irisshaders` and `net.coderbot` package names) and, independently, through an orthographic projection (`projection.m33 ~= 1`) outside the GUI context.

## Consequences
Every new item-driven manager must call the guard when queueing.
