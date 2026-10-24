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
- The Rhnull blood engine is the least developed system: its mechanics, how it is obtained and its recipe are still to design (planned for the 1.0 tutorial). Until then the engine and its pillar are creative only: the progression check counts mining a placed block as a source, so it does not see that nothing in survival gives you the first one.
- The Oni is deprecated and the dirty vena cava biome was cancelled. The mausoleum (its structure was never defined, only the templates and a biome tag exist), the Oni statue and the Sanguine Crucible core that only the Oni drops are deprecated with it and cannot be obtained in survival on purpose.
- Many creatures drop nothing on purpose, and some creatures with a spawn egg never appear in the world on purpose.

## Spells, books and staffs
- Every spell book is declared once in `SpellBookItems` (id, icon, cast type, affinity Blood Fire, Blood or Rhnull, Crimson Veil cost, charge and cooldown ticks). A book never takes Crimson Veil by itself; the cast does it once (ADR 0018).
- A book can be cast held alone in either hand, inside the Omnitome, with a staff in the main hand and the book in the off hand, or with a staff and the Omnitome in the off hand. `SpellCastGameTests` checks every book in each of those ways.
- The Crimson Veil HUD shows whenever a spell book, a staff, the Omnitome or a Crimson Veil weapon is in either hand. The set is the `crimson_veil_consumer` tag, filled from the registry.
- A staff has an affinity and a marker effect: the Scarlet Cinder favors Blood Fire books and counts targets with Blood Fire, the Incomplete Staff of the Pure Blooded favors Blood books and counts targets with Bleeding, the Golden Sorrow favors Rhnull books and charges as a kill streak instead of a marker: each kill by the player with it in either hand adds one charge up to five, a kill restarts a grace period of five seconds, and after that it loses one charge every three seconds (it keeps counting down while it is in the inventory and not selected). `KillStreak` holds the rule, `StaffKillStreakHandler` registers the kills.
- The server counts the living marked targets within 16 blocks every 10 ticks while the staff is selected and when a cast starts, up to five, and keeps the count in the staff. At five, a book of the staff affinity gets +30% damage, -25% Crimson Veil cost and -25% cooldown; a book of another affinity gets 40% of that. Any living entity with the marker effect counts, whatever it is; dying ones do not. The first guess of these numbers is in the constants of `BaseStaffItem`.
