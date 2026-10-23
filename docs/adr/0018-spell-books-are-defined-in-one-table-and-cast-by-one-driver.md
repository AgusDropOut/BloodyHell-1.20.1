# 0018. Spell books are defined in one table and cast by one driver

Status: Accepted

## Context
Every spell book repeated the same six getters (id, icon, cast type, cost, charge, cooldown) with its numbers written as constants inside the class, so the balance of the eleven books was spread over eleven files. The timing of a cast (when a sequential spell fires, when a thrown one fires, when a charged one fires on release) was written three times: in the book, in `BaseStaffItem` and in `OmnitomeItem`. The copies disagreed. A staff cast a sequential book twice on the same tick, and Sphere and Scratch took Crimson Veil inside `performSpell` on top of the charge in `executeCast`, so a cast cost twice what the book declared. Both were measured by `SpellCastGameTests` before the fix.

## Decision
- A spell book is a `SpellBookDefinition` (id, icon, cast type, affinity, cost, charge ticks, cooldown ticks) declared once, one line per book, in `SpellBookItems`. The book class keeps only what makes the spell different: `performSpell` and its particles and sounds. The base class answers `getCrimsonCost`, `getMinChargeTime`, `getCooldown` and the rest from the definition.
- `SpellCaster` is the only place that decides when a cast happens (`tick` and `release`). The book item, the staff and the Omnitome resolve which book and which hand they hold and delegate to it. `executeCast` in the base class stays the only place that charges Crimson Veil, performs the spell, triggers the animation and starts the cooldown.
- A book never touches Crimson Veil itself.

## Alternatives rejected
- Forge attributes or an extra gem in a copy of the stack to carry the staff bonus: they hide a dependency and add noise that has to be maintained.
- A `ThreadLocal` with the modifiers of the current cast: hidden state shared between the client and the server.

## Consequences
Adding a book is one line in the table plus its class; balancing is reading one table. `ResourceIntegrityTest` reads the table and checks that every book has its model, texture, icon and name. A modifier that applies to every cast, such as the staff affinity bonus, has one place to go. The animation code in the staff and the Omnitome still computes the cycle length by itself; it is rendering, not cast timing, and was left as it was.
