param(
    [Parameter(Mandatory = $true)][string]$Name,
    [switch]$Save,
    [switch]$Merge
)

$root = Split-Path -Parent $PSScriptRoot
$log = Join-Path $root "run\logs\latest.log"
$baselineDir = Join-Path $PSScriptRoot "gl-baseline"
$baseline = Join-Path $baselineDir ("probe-" + $Name + ".txt")

if (-not (Test-Path $log)) {
    Write-Host "No latest.log found. Run scripts/run-testworld.ps1 -GlProbe first."
    exit 1
}

$current = @(Select-String -Path $log -Pattern "\[GLPROBE\] " | ForEach-Object { ($_.Line -replace '^.*\[GLPROBE\] ', '').Trim() } | Sort-Object -Unique)
if ($current.Count -eq 0) {
    Write-Host "No [GLPROBE] lines in the log. The run needs -GlProbe and a loaded world."
    exit 1
}

if ($Merge -and (Test-Path $baseline)) {
    $merged = @(@(Get-Content $baseline) + $current | Sort-Object -Unique)
    Set-Content -Path $baseline -Value $merged -Encoding utf8
    Write-Host ("Merged: {0} transitions in {1}" -f $merged.Count, $baseline)
    exit 0
}

if ($Save -or $Merge) {
    New-Item -ItemType Directory -Force $baselineDir | Out-Null
    Set-Content -Path $baseline -Value $current -Encoding utf8
    Write-Host ("Saved {0} transitions to {1}" -f $current.Count, $baseline)
    exit 0
}

if (-not (Test-Path $baseline)) {
    Write-Host "No baseline at $baseline. Run with -Save on the unmodified code first."
    exit 1
}

$expected = @(Get-Content $baseline)
$onlyCurrent = @($current | Where-Object { $expected -notcontains $_ })
$onlyBaseline = @($expected | Where-Object { $current -notcontains $_ })
$labelsExpected = $expected | ForEach-Object { ($_ -split ' \| ')[0] } | Sort-Object -Unique
$labelsCurrent = $current | ForEach-Object { ($_ -split ' \| ')[0] } | Sort-Object -Unique
Write-Host ("Managers seen: baseline={0} current={1}" -f @($labelsExpected).Count, @($labelsCurrent).Count)
if ($onlyBaseline.Count -gt 0) {
    Write-Host ("{0} baseline transitions were not seen in this run (scene coverage, not a failure). Use -Merge to widen the baseline." -f $onlyBaseline.Count)
}
if ($onlyCurrent.Count -gt 0) {
    $onlyCurrent | ForEach-Object { Write-Host ("NEW transition: " + $_) }
    Write-Host ""
    Write-Host ("{0} transitions are not in the baseline." -f $onlyCurrent.Count)
    exit 2
}
Write-Host "No new GL state transitions."
exit 0
