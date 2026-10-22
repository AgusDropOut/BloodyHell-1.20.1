# 0013. The shader clock wraps every hour

Status: Accepted

## Context
`ShaderTime` wrapped the animation time every 100 seconds (ADR 0009 kept the wrap that the formula already had), which made every effect driven by it jump about every minute and a half. The time reaches the shaders as a 32 bit float, so the wrap exists to keep its resolution.

## Decision
`ShaderTime.WRAP_MILLIS` is one hour. A float near 3600 has a resolution of about 0.00024 s, and the largest multiplier the shaders apply to a `ShaderTime` uniform is 25 (`blood_blob`), which gives an error of about 0.006 radians, below what can be seen. The Insight shader is not affected: its time is the entity tick count.

## Consequences
The jump happens once per hour of continuous play instead of every 100 seconds. A shader that multiplies the time by something much larger than 25 would show jitter late in the hour; give it its own time or lower the wrap. Particles that use their own age are unaffected.
