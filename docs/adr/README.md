# Architecture Decision Records

One file per decision, named `NNNN-short-title.md`. Add a record only when the reason behind a decision cannot be recovered from reading the code. Never rewrite an accepted record: add a new one that supersedes it and change the old status to `Superseded by NNNN`.

Template: Title, Status, Context, Decision, Consequences.

| # | Decision |
|---|----------|
| 0001 | Deferred rendering for shader pack compatibility |
| 0002 | Manager call order is part of the behavior |
| 0003 | Skip deferred queueing during the shadow pass |
| 0004 | Capture projection and modelview matrices when queueing |
| 0005 | Shader animation time does not use the game time |
| 0006 | Raymarching through a quad |
| 0007 | Deferred manager refactor keeps the GL state calls untouched |
