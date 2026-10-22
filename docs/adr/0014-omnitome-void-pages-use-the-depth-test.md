# 0014. The Omnitome void pages use the depth test

Status: Accepted

## Context
The void page render type (`ModRenderTypes.getOmnitomeVoid`) was created with `NO_DEPTH_TEST`. The pages sit inside the closed book, but they were drawn after the book with no depth comparison, so they covered the closed cover.

## Decision
The render type keeps the default depth test and the void manager calls `RenderSystem.enableDepthTest()` after its batches, because a `RenderType` clear state leaves the depth test disabled.

## Consequences
The cover hides the pages when the book is closed and the pages show above the covers when it is open, in the hand and in the GUI, with and without a shader pack. If the pages ever disappear in the hand, the cause to check first is the depth values written by the book pass against the hand projection, not the render type.
