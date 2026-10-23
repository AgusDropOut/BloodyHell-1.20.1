# Design rules

Facts about how the mod is meant to work. Code and tests follow these; if the code disagrees, one of them is a bug.

## Materials and tiers
- Tool and armor order, lowest to highest: sanguinite, blasphemite, rhnull. Sanguinite and blasphemite are close (blasphemite slightly better); rhnull is clearly the strongest.
- Blasphemite blocks and ore need a sanguinite tool; the Rhnull block needs a blasphemite tool (`BlockToolRules`, ADR 0015).

## Fluids and machines
- Sanguinite machines that also exist in a rhnull version (pipes, tanks, condensers) cannot handle dangerous fluids, corrupted blood and viscous blasphemy: the tanks and the condensers refuse them and a pipe that ends up holding one bursts into a corrupted blood block after 60 ticks (`SanguinitePipeBlockEntity`). The rhnull version takes every fluid, so it is the one needed to move dangerous substances. The harvester and the infusor have no rhnull version and are not restricted (the harvester has to store the corrupted blood it makes).
- Blood, corrupted blood and visceral blood come from the blood harvester, depending on the soul type. Viscous blasphemy comes from the Unknown Portal machine.
- Rhnull comes from the sanguinite infusor: sanguinite with blood and visceral blood.

## Progression facts
- The Rhnull blood engine is the least developed system: its mechanics, how it is obtained and its recipe are still to design (planned for the 1.0 tutorial).
- The Oni is deprecated and the dirty vena cava biome was cancelled.
