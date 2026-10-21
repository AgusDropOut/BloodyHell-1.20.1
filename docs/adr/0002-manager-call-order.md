# 0002. Manager call order is part of the behavior

Status: Accepted

## Context
Objects composed of several deferred layers (e.g. the Omnitome) depend on the buffer and depth state left by the previous manager.

## Decision
The order of calls in `ModRenderEvents` is significant. The Omnitome is always Book, then Clip, then Void, and runs last.

## Consequences
Do not reorder managers without testing the Omnitome in first person and in the GUI.
