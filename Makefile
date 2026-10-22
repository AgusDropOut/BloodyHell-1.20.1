JAVA_HOME_17 ?= C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
EXIT_TICKS ?= 600
SHADER_PACKS ?= ComplementaryReimagined_r5.0.zip ComplementaryUnbound_r5.9.3.zip BSL_v10.0.zip

ifeq ($(OS),Windows_NT)
SHELL := powershell.exe
.SHELLFLAGS := -NoProfile -ExecutionPolicy Bypass -Command
GRADLE := $$env:JAVA_HOME='$(JAVA_HOME_17)'; .\gradlew.bat
TESTWORLD := .\scripts\run-testworld.ps1 -JavaHome '$(JAVA_HOME_17)' -ExitTicks $(EXIT_TICKS)
SYNC_SHADERS := New-Item -ItemType Directory -Force run/shaderpacks | Out-Null; foreach ($$p in '$(SHADER_PACKS)'.Split(' ')) { Copy-Item -LiteralPath (Join-Path $$env:APPDATA ('.minecraft/shaderpacks/' + $$p)) -Destination run/shaderpacks -Force }
else
GRADLE := ./gradlew
TESTWORLD := pwsh ./scripts/run-testworld.ps1 -ExitTicks $(EXIT_TICKS)
SYNC_SHADERS := mkdir -p run/shaderpacks && for p in $(SHADER_PACKS); do cp "$$HOME/.minecraft/shaderpacks/$$p" run/shaderpacks/; done
endif

.PHONY: help compile build test gametest check client client-shaders sync-shaders testworld testworld-shaders shader-cycle clean

help:
	@echo "compile             Compile the mod"
	@echo "build               Build the mod jar"
	@echo "test                Run unit tests"
	@echo "gametest            Run the GameTests (machines, every block) on a dedicated server"
	@echo "check               test + gametest + testworld, everything automatic before a push"
	@echo "client              Run the dev client"
	@echo "client-shaders      Run the dev client with Oculus and Rubidium"
	@echo "sync-shaders        Copy SHADER_PACKS from the normal .minecraft into run/shaderpacks"
	@echo "testworld           Run the automated test world and scan the log (EXIT_TICKS=600)"
	@echo "testworld-shaders   Same as testworld, with Oculus and Rubidium"
	@echo "shader-cycle        Switch through every pack in run/shaderpacks at runtime and look for crashes"
	@echo "clean               Remove build output"

compile:
	$(GRADLE) compileJava

build:
	$(GRADLE) build

test:
	$(GRADLE) test

gametest:
	$(GRADLE) runGameTestServer

check: test gametest testworld

client:
	$(GRADLE) runClient

client-shaders: sync-shaders
	$(GRADLE) runClient -PbhShaders

sync-shaders:
	$(SYNC_SHADERS)

testworld:
	$(TESTWORLD)

testworld-shaders: sync-shaders
	$(TESTWORLD) -Shaders

shader-cycle: sync-shaders
	$(TESTWORLD) -ShaderCycle

clean:
	$(GRADLE) clean
