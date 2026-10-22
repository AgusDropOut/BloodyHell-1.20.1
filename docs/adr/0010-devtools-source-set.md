# 0010. Test tooling lives in a separate source set

Status: Accepted

## Context
The test world command, the auto test, the shader pack cycle and the GL state probe are only useful in development. Inside `src/main` they were compiled into the release jar. They were inactive there (system properties, `FMLEnvironment.production`), but they shipped anyway.

## Decision
The code is in the `devtools` source set (`src/devtools/java`, same package). `build.gradle` compiles it against the main output and adds it to the `mods` block of the run configurations, so Forge scans it as part of the mod in development. The `jar` task only packs `main`.
The main code does not reference it. The GL probe is installed through `DeferredRenderRegistry.setObserver` by `DevToolsBootstrap` on client setup, only when `-PbhGlProbe` is set.

## Consequences
The IDE needs a Gradle refresh after the source set was added. A release can be checked with `jar tf build/libs/<mod>.jar`, which must list no `devtools` entry. Any new test tool goes in the source set, and if main code needs a hook it gets an interface in main, like `DeferredRenderObserver`.
