# TODO

Pending work. Remove an item when it is done; move any lasting reasoning to `docs/adr/`.

## Render refactor (next commit)
- [ ] Fix every item in `docs/TECH_DEBT.md`.
- [ ] Introduce a `DeferredRenderManager` interface with an ordered registry.
- [ ] Shared scene-color copy for the raymarching managers.
- [ ] Single `ShaderTime` helper.

## Testing
- [ ] Static test of registries and resources (renderers, layers, textures, models, geo, animations, lang, shaders).
- [x] Debug command to generate the test world (`/bhtest build`).
- [x] Log scan for exceptions after running the test world (`scripts/run-testworld.ps1`).
- [ ] Complete the manual checklist in `docs/TESTING.md` as systems are verified.
- [x] Run the test world for the first time (baseline clean, see `docs/TESTING.md`).
- [ ] Fix items 7 to 16 of `docs/TECH_DEBT.md`.
- [ ] Walk the entity grid with and without a shader pack.
- [ ] GameTests for complex logic (condenser, plants, engine, blood pipes).

## Documentation
- [ ] Lore in `docs/`.
- [ ] Review ADRs after the render refactor.

## Next session (resume here)

State at the end of the last session:
- Everything is committed in three commits: entity fixes, test tooling and docs, client package refactor. Working tree is clean except this file.
- The test world baseline is clean (see `docs/TESTING.md`). Compiles with JDK 17 only.

Suggested order:
1. Walk the entity grid in game, with and without a shader pack, and tick the checklist in `docs/TESTING.md`.
2. Attack `docs/TECH_DEBT.md` items 1 to 6 (render managers), then 7 to 16.
3. Write the ADR for each debt item fixed that changes a decision (for example the shared scene-color copy, a single `ShaderTime`, the ordered manager registry).

Decisions made:
- `/bhtest` is disabled in the released mod (`FMLEnvironment.production` check).
- `target` of `UnknownEntityArms` and `SanguineSacrificeEntity` is not persisted; these entities may vanish on reload.
- The fluid blockstate warnings have always existed; they stay in the known-warnings list and may be fixed later.
- `scripts/` and `devtools/` are shared with the team in the repo.

Open questions for the user:
- The Lore file: where in `docs/` and in what format?
- Should I add a static JUnit test for registries and resources (renderers, textures, models, geo, lang, shaders) now that the test world exists?
