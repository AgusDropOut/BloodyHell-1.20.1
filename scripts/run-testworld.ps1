param(
    [string]$JavaHome = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot",
    [int]$ExitTicks = 600
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

$log = Join-Path $root "run\logs\latest.log"
if (Test-Path $log) {
    Remove-Item $log -Force -ErrorAction SilentlyContinue
}

Push-Location $root
try {
    & .\gradlew.bat runClient -PbhAutoTest "-PbhExitTicks=$ExitTicks"
    $gradleExit = $LASTEXITCODE
} finally {
    Pop-Location
}

if ($gradleExit -ne 0) {
    Write-Host "Gradle failed with exit code $gradleExit."
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
