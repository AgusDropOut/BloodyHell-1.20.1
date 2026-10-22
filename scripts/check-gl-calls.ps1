param(
    [switch]$Snapshot,
    [string]$Accept,
    [string]$Retire
)

$root = Split-Path -Parent $PSScriptRoot
$renderDir = Join-Path $root "src\main\java\net\agusdropout\bloodyhell\client\render"
$managerDirs = @("effect", "deferred", "item\deferred") | ForEach-Object { Join-Path $renderDir $_ }

function Find-Source($name) {
    foreach ($dir in $managerDirs) {
        $found = Get-ChildItem $dir -Recurse -Filter ($name + ".java") -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) { return $found.FullName }
    }
    return Join-Path $renderDir ($name + ".java")
}
$baselineDir = Join-Path $PSScriptRoot "gl-baseline\calls"

$callPattern = 'RenderSystem\.|GlStateManager\.|\bGL\d+C?\.|getUniform\(|bindTexture|blendFunc'
$scaffoldingPattern = 'getProjectionMatrix\(\)|getModelViewMatrix\(\)|getModelViewStack\(\)|setProjectionMatrix\(|applyModelViewMatrix\(\)'

function Get-Calls($file, [bool]$skipScaffolding) {
    $lines = Get-Content $file | ForEach-Object { ($_ -replace '\s+', ' ').Trim() } | Where-Object { $_ -match $callPattern }
    if ($skipScaffolding) {
        $lines = $lines | Where-Object { $_ -notmatch $scaffoldingPattern }
    }
    return @($lines)
}

if ($Retire) {
    Remove-Item (Join-Path $baselineDir ($Retire + ".txt")) -ErrorAction Stop
    Write-Host "Retired baseline for $Retire"
    exit 0
}

if ($Accept) {
    New-Item -ItemType Directory -Force $baselineDir | Out-Null
    $calls = Get-Calls (Find-Source $Accept) $false
    Set-Content -Path (Join-Path $baselineDir ($Accept + ".txt")) -Value $calls -Encoding utf8
    Write-Host "Accepted baseline for $Accept"
    exit 0
}

if ($Snapshot) {
    New-Item -ItemType Directory -Force $baselineDir | Out-Null
    $managerDirs | ForEach-Object { Get-ChildItem $_ -Recurse -Filter *.java } | ForEach-Object {
        $calls = Get-Calls $_.FullName $false
        Set-Content -Path (Join-Path $baselineDir ($_.BaseName + ".txt")) -Value $calls -Encoding utf8
    }
    Write-Host "Baseline written to $baselineDir"
    exit 0
}

$failed = 0
Get-ChildItem $baselineDir -Filter *.txt | ForEach-Object {
    $name = $_.BaseName
    $source = Find-Source $name
    $expected = @(Get-Content $_.FullName | Where-Object { $_ -notmatch $scaffoldingPattern })
    if (-not (Test-Path $source)) {
        Write-Host "MISSING  $name (source file no longer exists; update the baseline if it was moved on purpose)"
        $script:failed++
        return
    }
    $actual = Get-Calls $source $true
    if (($expected -join "`n") -ne ($actual -join "`n")) {
        Write-Host "CHANGED  $name"
        $differences = @(Compare-Object -ReferenceObject $expected -DifferenceObject $actual)
        if ($differences.Count -eq 0) {
            Write-Host "  same calls, different order"
        }
        $differences | ForEach-Object {
            $marker = if ($_.SideIndicator -eq '<=') { '  - ' } else { '  + ' }
            Write-Host ($marker + $_.InputObject)
        }
        $script:failed++
    }
}

if ($failed -gt 0) {
    Write-Host ""
    Write-Host "$failed manager(s) differ from the GL call baseline."
    exit 2
}
Write-Host "All GL state calls match the baseline (matrix scaffolding ignored)."
