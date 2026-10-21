# 0005. Shader animation time does not use the game time

Status: Accepted

## Context
Using the game time for animation uniforms is sometimes overridden by Minecraft and produces broken animation.

## Decision
Animations are driven by wall-clock time (`Util.getMillis()`) or, for particles, by `age + partialTicks`.

## Consequences
Time sources are currently duplicated and inconsistent, see `docs/TECH_DEBT.md`.
