param(
    [Parameter(Mandatory = $true)][string]$Name,
    [switch]$Save
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

if ($Save) {
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
$differences = @(Compare-Object -ReferenceObject $expected -DifferenceObject $current)
$labelsExpected = $expected | ForEach-Object { ($_ -split ' \| ')[0] } | Sort-Object -Unique
$labelsCurrent = $current | ForEach-Object { ($_ -split ' \| ')[0] } | Sort-Object -Unique
Write-Host ("Managers seen: baseline={0} current={1}" -f @($labelsExpected).Count, @($labelsCurrent).Count)
if ($differences.Count -eq 0) {
    Write-Host "GL state transitions match the baseline."
    exit 0
}
$differences | ForEach-Object {
    $marker = if ($_.SideIndicator -eq '<=') { 'only in baseline: ' } else { 'only in current:  ' }
    Write-Host ($marker + $_.InputObject)
}
exit 2
