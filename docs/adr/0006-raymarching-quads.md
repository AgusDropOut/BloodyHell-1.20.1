# 0006. Raymarching through a quad

Status: Accepted

## Context
Raymarched effects (blood blob, orbs) need a surface to run a fragment shader and must intersect correctly with the world.

## Decision
Draw a quad and raymarch in the fragment shader, reading the scene color (a copy of the main target, `Sampler0`) and the depth (`Sampler1`).
- Blob / MiniBlob: full-screen quad in NDC. The ray is rebuilt with `u_InvProjMat` and `u_CleanInvViewMat`. The clean view matrix has no view bobbing, and `u_BobbingOffset` compensates the camera position so the effect does not jitter.
- Orbs (Blood, GoldenSorrow, ScarletCinder): billboard quad in view space around the orb center, using the projection captured for that orb (see 0004) so it also works in the hand.

## Consequences
Each raymarcher currently copies the main target itself, see `docs/TECH_DEBT.md`.
