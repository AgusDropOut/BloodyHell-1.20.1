JAVA_HOME_17 ?= C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
EXIT_TICKS ?= 600

ifeq ($(OS),Windows_NT)
SHELL := powershell.exe
.SHELLFLAGS := -NoProfile -ExecutionPolicy Bypass -Command
GRADLE := $$env:JAVA_HOME='$(JAVA_HOME_17)'; .\gradlew.bat
TESTWORLD := .\scripts\run-testworld.ps1 -JavaHome '$(JAVA_HOME_17)' -ExitTicks $(EXIT_TICKS)
else
GRADLE := ./gradlew
TESTWORLD := pwsh ./scripts/run-testworld.ps1 -ExitTicks $(EXIT_TICKS)
endif

.PHONY: help compile build test gametest testworld clean

help:
	@echo "compile    Compile the mod"
	@echo "build      Build the mod jar"
	@echo "test       Run unit tests"
	@echo "gametest   Run the Forge GameTest server"
	@echo "testworld  Run the automated test world and scan the log (EXIT_TICKS=600)"
	@echo "clean      Remove build output"

compile:
	$(GRADLE) compileJava

build:
	$(GRADLE) build

test:
	$(GRADLE) test

gametest:
	$(GRADLE) runGameTestServer

testworld:
	$(TESTWORLD)

clean:
	$(GRADLE) clean
