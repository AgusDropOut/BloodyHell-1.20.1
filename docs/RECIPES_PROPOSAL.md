# Recipe proposals

Proposals for the items that have no recipe yet. Nothing here is implemented: it waits for review. Delete a section when its recipe is written in `ModRecipes`.

Constraints of the stations:
- Blood altar ritual: exactly 4 offerings, one per altar, order does not matter, activated with a corrupted blood flask.
- Blasphemous altar ritual: up to 3 offerings, the same set on the 4 altars, activated with a filled blood flask.
- Both are written with `BloodAltarRecipeBuilder.ritual(...)` and `BlasphemousBloodAltarRecipeBuilder.ritual(...)`.

## Incomplete Staff of the Pure Blooded
The first staff, early game.
- Blood altar: stick, filled blood flask, ancient gem, pure blood gem.
- Why: same tier as the Blood Sphere spell book (flask, dagger, ancient gem), and the pure blood gem ties it to its bonus (bleeding mobs).

## Staff of Scarlet Cinder
- Blood altar: Incomplete Staff, cinder acolyte fainted ember, ritekeeper heart, filled rhnull blood flask.
- Why: the sanctum drops (ritekeeper heart, ember) only feed the Blood Fire books today; the staff is the other Blood Fire item.
- Open: an upgrade of the Incomplete Staff (as written) or its own recipe.

## Staff of the Golden Sorrow
- Blood altar: stick, ancient rhnull gem, filled rhnull blood flask, gold block.
- Why: the same shape as the Golden Throne spell book recipe (book, ancient rhnull gem, filled rhnull flask, gold block).
- Alternative on the blasphemous altar (3 offerings): ancient rhnull gem, filled rhnull blood flask, gold block, with no stick.

## The Omnitome
The last item of the book line.
- Blasphemous altar (3 offerings): a spell book, great ancient blasphemous gem, Gaze of the Unknown.
- Alternative on the blood altar (4 offerings): book, great ancient blasphemous gem, Gaze of the Unknown, blasphemite block.
- Open: which spell book, or any of them.

## Not decided
- `crimson_shell` has no recipe that uses it.
- The Rhnull blood engine has no recipe either (its design is open, see `docs/DESIGN.md`).
