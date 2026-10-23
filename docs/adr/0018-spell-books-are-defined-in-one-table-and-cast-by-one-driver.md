# 0018. Spell books are defined in one table and cast by one driver

Status: Accepted

## Context
Every spell book repeated the same six getters (id, icon, cast type, cost, charge, cooldown) with its numbers written as constants inside the class, so the balance of the eleven books was spread over eleven files. The timing of a cast (when a sequential spell fires, when a thrown one fires, when a charged one fires on release) was written three times: in the book, in `BaseStaffItem` and in `OmnitomeItem`. The copies disagreed. A staff cast a sequential book twice on the same tick, and Sphere and Scratch took Crimson Veil inside `performSpell` on top of the charge in `executeCast`, so a cast cost twice what the book declared. Both were measured by `SpellCastGameTests` before the fix.

## Decision
- A spell book is a `SpellBookDefinition` (id, icon, cast type, affinity, cost, charge ticks, cooldown ticks) declared once, one line per book, in `SpellBookItems`. The book class keeps only what makes the spell different: `performSpell` and its particles and sounds. The base class answers `getCrimsonCost`, `getMinChargeTime`, `getCooldown` and the rest from the definition.
- `SpellCaster` is the only place that decides when a cast happens (`tick` and `release`). The book item, the staff and the Omnitome resolve which book and which hand they hold and delegate to it. `executeCast` in the base class stays the only place that charges Crimson Veil, performs the spell, triggers the animation and starts the cooldown.
- A book never touches Crimson Veil itself.
- A cast receives a `SpellCastContext` (level, player, book, stack, hand and the `SpellModifiers` of the focus). The book spawns its projectiles with `ctx.spawn(...)`, which scales the damage through `IGemSpell`, and the base class scales cost and cooldown from the same modifiers.
- A staff has an affinity and an optional marker effect. While it is the selected item, the server counts every living entity within 16 blocks that has the marker effect (up to five) every 10 ticks and stores the number in the stack. The cast, the tooltip and the future GUI meter and staff orb read that number (`BaseStaffItem.getBonusFill`, 0 to 1), so there is one value and it reaches the client with the normal inventory sync. The count is not refreshed while the staff is being used, so a sequential channel keeps a stable cycle. A book of the staff affinity gets the full bonus, any other book 40% of it.
- Staffs, spell books and the Omnitome extend `BaseGeckoItem`, which owns the GeckoLib instance cache, the tick and the synced registration, and asks every item for its `getId()`. `BaseSpellBookItem` lost its unused type parameter. The look of each staff (model and orb effect) is one entry of the table in `StaffRenderers`, the same way a book is one entry of `SpellBookItems`; a new staff needs its entry there and no renderer class. The same goes for armor sets (`BaseGeckoArmorItem` with a `Look` of asset name, animation and glow) and for the combo weapons, which implement `GeckoAsset` and use the generic item model and renderer.

## Alternatives rejected
- Forge attributes or an extra gem in a copy of the stack to carry the staff bonus: they hide a dependency and add noise that has to be maintained.
- A `ThreadLocal` with the modifiers of the current cast: hidden state shared between the client and the server.

## Consequences
Adding a book is one line in the table plus its class; balancing is reading one table. `ResourceIntegrityTest` reads the table and checks that every book has its model, texture, icon and name. A modifier that applies to every cast has one place to go: the staff bonus only passes its `SpellModifiers` through `SpellCaster` and the context, and the books changed once, mechanically, to receive the context. The animation code in the staff and the Omnitome still computes the cycle length by itself; it is rendering, not cast timing, and was left as it was.
