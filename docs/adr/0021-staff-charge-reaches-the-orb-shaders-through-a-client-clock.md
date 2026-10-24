# 0021. Staff charge reaches the orb shaders through a client clock

Status: Accepted

## Context
A staff fills as the number of marked targets near the player grows (`BaseStaffItem.getBonusFill`, 0 to 1 in steps of 0.2). The author wanted the player to see and hear it. A 32x32 pixel-art meter drawn with the orb shaders into a small render target was built and dropped on 2026-10-23: it worked but a flat HUD element did not fit the mod, whose identity is the deferred ray-marched orbs.

The orb shaders animate with `u_Time`. If the speed of an animation depends on the charge and the shader computes `u_Time * speed(charge)`, every change of the charge shifts the phase of the whole animation, so the orb jumps instead of accelerating.

## Decision
- The orb reacts by itself. `StaffChargeClient` (client only) reads the marked targets of the staff in hand and gives each frame an `OrbCharge`: `charge` (the fill, smoothed in about half a second), `clock` and `flash`. `StaffOrbLayer` passes it to `OrbRenderManager`, which sets the uniforms `u_Charge`, `u_ChargeTime` and `u_Flash`.
- `u_ChargeTime` is a clock accumulated on the CPU at a speed of `1 + boost * charge`, where the boost is 1.5 for the cinder and the golden orb and 0.6 for the blood orb (its pulse tops out at about 2.4 beats per second instead of 3.75). The shaders use it wherever they used `u_Time`, so the speed changes without a jump. `u_Charge` drives the static changes (volume, groove depth, number of blobs, sharpness) and `u_Flash` a one-time pulse when the charge reaches full.
- Blobs added by a higher charge (the cinder goes from 8 to 16) grow in with a weight instead of appearing, because a blob that appears in the middle of its life pops.
- The sounds are played on the client of the holder only, from `StaffChargeSoundPlan` (a pure function, unit tested): a note that rises for each new target, a sound at full, a fade when leaving full or reaching zero, never more than one sound per change, silence for the intermediate drops. The Scarlet Cinder also has a quiet ember loop while full. The blood orb has a heartbeat that follows its pulse: `OrbHeartbeat` finds the moments when the clock crosses the peaks of the two beats of the shader (phases 0.35 and 0.5 of each pulse cycle) and the client plays a soft and a strong thump at those moments, only while the charge is full. Nothing is sent over the network.

## Consequences
- No GUI element and no packet: the stack already syncs the marked count.
- A new orb shader only has to declare the three uniforms in its json and use `u_ChargeTime` instead of `u_Time`.
- The charge is only visible in first person, like the orb itself, and only for the staff in the hand of the local player.
- The Golden Sorrow charges as a kill streak (`KillStreak`): the client reads the same `Charge` of the stack, so it needs nothing special.
