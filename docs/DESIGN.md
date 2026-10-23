# Design rules

Facts about how the mod is meant to work. Code and tests follow these; if the code disagrees, one of them is a bug.

## Materials and tiers
- Tool and armor order, lowest to highest: sanguinite, blasphemite, rhnull. Sanguinite and blasphemite are close (blasphemite slightly better); rhnull is clearly the strongest.
- Blasphemite blocks and ore need a sanguinite tool; the Rhnull block needs a blasphemite tool (`BlockToolRules`, ADR 0015).

## Fluids and machines
- Sanguinite machines (pipes, tanks, harvester, infusor, condenser) cannot hold corrupted blood or viscous blasphemy: those fluids are too aggressive for the metal. Rhnull is the stronger material and its machines can hold them. Today only the data is there (`isValidFluid` of the condensers); the rule is not applied to the other machines yet (see `docs/TECH_DEBT.md`).
- Blood, corrupted blood and visceral blood come from the blood harvester, depending on the soul type. Viscous blasphemy comes from the Unknown Portal machine.
- Rhnull comes from the sanguinite infusor: sanguinite with blood and visceral blood.

## Progression facts
- The Rhnull blood engine is the least developed system: its mechanics, how it is obtained and its recipe are still to design (planned for the 1.0 tutorial).
- The Oni is deprecated and the dirty vena cava biome was cancelled.
