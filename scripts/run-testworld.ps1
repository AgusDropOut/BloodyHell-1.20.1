param(
    [string]$JavaHome = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot",
    [int]$ExitTicks = 600,
    [switch]$Shaders,
    [switch]$ShaderCycle
)

$env:JAVA_HOME = $JavaHome
$env:Path = "$JavaHome\bin;$env:Path"
$root = Split-Path -Parent $PSScriptRoot
$world = Join-Path $root "run\saves\BHTestWorld"

if (-not (Test-Path $world)) {
    Write-Host "World 'BHTestWorld' not found in run\saves."
    Write-Host "Create it once: Singleplayer > Create New World > name it BHTestWorld, Creative, Allow Cheats ON, World Type Superflat."
    exit 1
}

$startedAt = Get-Date
$log = Join-Path $root "run\logs\latest.log"
if (Test-Path $log) {
    Remove-Item $log -Force -ErrorAction SilentlyContinue
}

Push-Location $root
try {
    $gradleArgs = @("runClient", "-PbhAutoTest", "-PbhExitTicks=$ExitTicks")
    if ($Shaders -or $ShaderCycle) { $gradleArgs += "-PbhShaders" }
    if ($ShaderCycle) {
        $packsDir = Join-Path $root "run\shaderpacks"
        $packs = @(Get-ChildItem $packsDir -ErrorAction SilentlyContinue | ForEach-Object { $_.Name })
        if ($packs.Count -eq 0) {
            Write-Host "No shader packs in run\shaderpacks. Run 'make sync-shaders' first."
            exit 1
        }
        Write-Host ("Cycling shader packs: " + ($packs -join ", "))
        $gradleArgs += "-PbhShaderCycle=$($packs -join ',')"
    }
    & .\gradlew.bat @gradleArgs
    $gradleExit = $LASTEXITCODE
} finally {
    Pop-Location
}

if ($gradleExit -ne 0) {
    Write-Host "Gradle failed with exit code $gradleExit."
    $crash = Get-ChildItem (Join-Path $root "run\crash-reports") -ErrorAction SilentlyContinue | Where-Object { $_.LastWriteTime -gt $startedAt } | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($crash) {
        Write-Host ("Crash report: " + $crash.FullName)
        Select-String -Path $crash.FullName -Pattern "^Description:|^Caused by:|^java\.|^net\." | Select-Object -First 6 | ForEach-Object { $_.Line }
    }
    exit 1
}

if (-not (Test-Path $log)) {
    Write-Host "No latest.log found."
    exit 1
}

Write-Host ""
Write-Host "===== BHTEST summary ====="
Select-String -Path $log -Pattern "\[BHTEST\]" | ForEach-Object { $_.Line }

Write-Host ""
Write-Host "===== Errors and exceptions ====="
$knownFile = Join-Path $PSScriptRoot "testworld-known-warnings.txt"
$known = @()
if (Test-Path $knownFile) {
    $known = Get-Content $knownFile | Where-Object { $_.Trim() -ne "" }
}
$problems = Select-String -Path $log -Pattern "/ERROR\]|/FATAL\]|/WARN\]|Exception|Missing (texture|model)|Failed to" | Where-Object {
    $line = $_.Line
    if ($line -match "\[BHTEST\] built") { return $false }
    foreach ($pattern in $known) {
        if ($line -match $pattern) { return $false }
    }
    return $true
}
if ($problems) {
    $problems | ForEach-Object { $_.Line }
    Write-Host ""
    Write-Host ("Found {0} suspicious lines." -f @($problems).Count)
    exit 2
}
Write-Host "No errors found."
