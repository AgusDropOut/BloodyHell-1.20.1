# Contributing

## Data generation
- Prefer data generators over hand-written resource or data files (blockstates, models, recipes, tags, loot tables, lang).
- Everything under `src/generated/resources` is produced only by `./gradlew runData` and is never edited by hand.
- A hand-written file is acceptable only when it is too custom for a generator (for example GeckoLib geo and animations, shaders, particle definitions, calibrated item model displays). Say why in the commit message when adding one.
