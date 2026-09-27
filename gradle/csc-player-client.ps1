param(
    [Parameter(Mandatory = $true)][string]$LatestLaunchBat,
    [Parameter(Mandatory = $true)][string]$MinecraftDir,
    [Parameter(Mandatory = $true)][string]$ArtifactPath,
    [Parameter(Mandatory = $true)][string]$RuntimeRoot,
    [ValidateRange(1, 512)][int]$CacheMaxGiB = 24,
    [switch]$NoLaunch
)

$ErrorActionPreference = 'Stop'

function Write-Utf8NoBom {
    param([string]$Path, [string]$Text)
    $encoding = [System.Text.UTF8Encoding]::new($false)
    [System.IO.File]::WriteAllText($Path, $Text, $encoding)
}

function Get-PlayerProfileFingerprint {
    param([string]$MinecraftRoot)

    $hash = [System.Security.Cryptography.IncrementalHash]::CreateHash([System.Security.Cryptography.HashAlgorithmName]::SHA256)
    $encoding = [System.Text.Encoding]::UTF8
    $add = {
        param([string]$value)
        $hash.AppendData($encoding.GetBytes($value + "`n"))
    }

    & $add 'csc-player-runtime-v1'
    foreach ($relativeRoot in @('mods', 'config', 'defaultconfigs', 'kubejs', 'resourcepacks', 'shaderpacks', 'datapacks')) {
        $path = Join-Path $MinecraftRoot $relativeRoot
        if (!(Test-Path -LiteralPath $path)) { continue }
        # A real player instance may have mod-owned runtime caches beneath mods
        # (for example MCEF's browser cache).  They are neither loader inputs nor
        # stable, so only jar payloads participate in the profile key.
        $files = if ($relativeRoot -eq 'mods') {
            Get-ChildItem -LiteralPath $path -File -Recurse |
                Where-Object { $_.Name -match '(?i)\.jar(?:\.disabled)?$' }
        } else {
            Get-ChildItem -LiteralPath $path -File -Recurse
        }
        $files | Sort-Object FullName | ForEach-Object {
            $relative = $_.FullName.Substring($MinecraftRoot.Length).TrimStart('\')
            & $add $relative
            & $add $_.Length.ToString()
            & $add (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash
        }
    }
    foreach ($name in @('options.txt', 'optionsof.txt', 'servers.dat', 'servers.dat_old')) {
        $path = Join-Path $MinecraftRoot $name
        if (Test-Path -LiteralPath $path) {
            & $add $name
            & $add (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash
        }
    }
    return ([Convert]::ToHexString($hash.GetHashAndReset())).ToLowerInvariant()
}

function Ensure-Junction {
    param([string]$LinkPath, [string]$TargetPath)

    $resolvedTarget = (Resolve-Path -LiteralPath $TargetPath).Path.TrimEnd('\')
    if (Test-Path -LiteralPath $LinkPath) {
        $existing = Get-Item -LiteralPath $LinkPath -Force
        if (($existing.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -eq 0) {
            throw "Refusing to replace non-junction cache path: $LinkPath"
        }
        $actualTargets = @($existing.Target | ForEach-Object { $_.ToString().TrimEnd('\') })
        if ($actualTargets -notcontains $resolvedTarget) {
            throw "Refusing to reuse junction with a different target: $LinkPath"
        }
        return
    }
    New-Item -ItemType Junction -Path $LinkPath -Target $resolvedTarget | Out-Null
}

function Copy-ModsReadOnly {
    param([string]$SourcePath, [string]$DestinationPath)

    New-Item -ItemType Directory -Force -Path $DestinationPath | Out-Null
    # Nested folders in mods are sometimes used as mutable caches. The loader
    # only consumes jars, so preserve nested jars but never copy arbitrary cache
    # files into a reusable player-runtime instance.
    Get-ChildItem -LiteralPath $SourcePath -File -Recurse |
        Where-Object { $_.Name -match '(?i)\.jar(?:\.disabled)?$' } |
        ForEach-Object {
            if ($_.Name -match '(?i)createschematicchecker.*\.jar$') { return }
            $relative = $_.FullName.Substring($SourcePath.Length).TrimStart('\\')
            $destination = Join-Path $DestinationPath $relative
            New-Item -ItemType Directory -Force -Path (Split-Path -Parent $destination) | Out-Null
            try {
                New-Item -ItemType HardLink -Path $destination -Target $_.FullName -ErrorAction Stop | Out-Null
            } catch {
                # Cross-volume sources cannot hard-link; a normal copy is the safe fallback.
                Copy-Item -LiteralPath $_.FullName -Destination $destination -Force
            }
        }
}

function Get-DirectoryBytes {
    param([string]$Path)

    # Never traverse assets/libraries/versions junctions while accounting the
    # cache. They belong to the read-only player baseline, not this cache layer.
    [int64]$total = 0
    $pending = [System.Collections.Generic.Stack[string]]::new()
    $pending.Push($Path)
    while ($pending.Count -gt 0) {
        $current = $pending.Pop()
        foreach ($entry in @(Get-ChildItem -LiteralPath $current -Force -ErrorAction SilentlyContinue)) {
            if ($entry.PSIsContainer) {
                if (($entry.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -eq 0) {
                    $pending.Push($entry.FullName)
                }
            } else {
                $total += [int64]$entry.Length
            }
        }
    }
    return $total
}

function Trim-PlayerCache {
    param([string]$InstancesPath, [string]$ActiveKey, [int64]$MaximumBytes)

    $instances = @(Get-ChildItem -LiteralPath $InstancesPath -Directory -Force | ForEach-Object {
        [pscustomobject]@{ Item = $_; Bytes = Get-DirectoryBytes $_.FullName }
    })
    [int64]$total = ($instances | Measure-Object -Property Bytes -Sum).Sum
    foreach ($entry in ($instances | Where-Object { $_.Item.Name -ne $ActiveKey } | Sort-Object { $_.Item.LastWriteTimeUtc })) {
        if ($total -le $MaximumBytes) { break }
        $resolved = (Resolve-Path -LiteralPath $entry.Item.FullName).Path
        $expectedParent = (Resolve-Path -LiteralPath $InstancesPath).Path.TrimEnd('\') + '\'
        if (!$resolved.StartsWith($expectedParent, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw "Refusing cache eviction outside instances root: $resolved"
        }
        Remove-Item -LiteralPath $resolved -Recurse -Force
        $total -= $entry.Bytes
    }
}

foreach ($path in @($LatestLaunchBat, $MinecraftDir, $ArtifactPath)) {
    if (!(Test-Path -LiteralPath $path)) { throw "Required path does not exist: $path" }
}

$sourceMinecraft = (Resolve-Path -LiteralPath $MinecraftDir).Path.TrimEnd('\')
$launchBat = (Resolve-Path -LiteralPath $LatestLaunchBat).Path
$artifact = (Resolve-Path -LiteralPath $ArtifactPath).Path
foreach ($immutable in @('assets', 'libraries', 'versions')) {
    if (!(Test-Path -LiteralPath (Join-Path $sourceMinecraft $immutable))) {
        throw "Normal launcher instance is incomplete; missing $immutable under $sourceMinecraft"
    }
}
if (!(Test-Path -LiteralPath (Join-Path $sourceMinecraft 'mods'))) {
    throw "Normal launcher instance is incomplete; missing mods under $sourceMinecraft"
}
$launchText = Get-Content -LiteralPath $launchBat -Raw
if ($launchText -notmatch [regex]::Escape($sourceMinecraft)) {
    throw "LatestLaunch BAT does not reference the supplied MinecraftDir: $launchBat"
}
if ($launchText -match '(?i)cpw\.mods\.bootstraplauncher\.BootstrapLauncher|--launchTarget\s+(?:forge|neoforge)client|net\.neoforged') {
    throw "CSC Fabric runClient requires a Fabric launcher instance; the supplied BAT is Forge/NeoForge: $launchBat"
}
if ($launchText -notmatch '(?i)net\.fabricmc\.loader\.impl\.launch\.knot\.KnotClient|net\.fabricmc\.devlaunchinjector\.Main') {
    throw "CSC Fabric runClient requires a normal Fabric launcher instance; the supplied BAT is not a Fabric launch: $launchBat"
}

$runtime = [System.IO.Path]::GetFullPath($RuntimeRoot).TrimEnd('\')
if ($runtime.Equals($sourceMinecraft, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw 'RuntimeRoot must be a CSC test cache, not the player launcher instance.'
}
$instancesRoot = Join-Path $runtime 'instances'
$locksRoot = Join-Path $runtime 'locks'
New-Item -ItemType Directory -Force -Path $instancesRoot, $locksRoot | Out-Null

$fingerprint = Get-PlayerProfileFingerprint -MinecraftRoot $sourceMinecraft
$lockPath = Join-Path $locksRoot "$fingerprint.lock"
$lock = $null
for ($attempt = 0; $attempt -lt 60 -and $null -eq $lock; $attempt++) {
    try { $lock = [System.IO.File]::Open($lockPath, [System.IO.FileMode]::CreateNew, [System.IO.FileAccess]::Write, [System.IO.FileShare]::None) }
    catch [System.IO.IOException] { Start-Sleep -Seconds 1 }
}
if ($null -eq $lock) { throw "Timed out waiting for CSC player-runtime cache lock: $lockPath" }

try {
    $instanceRoot = Join-Path $instancesRoot $fingerprint
    $testMinecraft = Join-Path $instanceRoot '.minecraft'
    $marker = Join-Path $instanceRoot 'runtime-manifest.json'
    if (!(Test-Path -LiteralPath $marker)) {
        New-Item -ItemType Directory -Force -Path $testMinecraft | Out-Null
        foreach ($immutable in @('assets', 'libraries', 'versions')) {
            Ensure-Junction -LinkPath (Join-Path $testMinecraft $immutable) -TargetPath (Join-Path $sourceMinecraft $immutable)
        }
        # Copy only stable player-facing state. The rest of a live instance
        # (worlds, logs, downloads, browser/shader caches and crash reports) is
        # deliberately absent from the test cache.
        foreach ($directory in @('mods', 'config', 'defaultconfigs', 'kubejs', 'resourcepacks', 'shaderpacks', 'datapacks')) {
            $source = Join-Path $sourceMinecraft $directory
            if (!(Test-Path -LiteralPath $source)) { continue }
            $destination = Join-Path $testMinecraft $directory
            if ($directory -eq 'mods') {
                Copy-ModsReadOnly -SourcePath $source -DestinationPath $destination
            } else {
                Copy-Item -LiteralPath $source -Destination $destination -Recurse -Force
            }
        }
        foreach ($file in @('options.txt', 'optionsof.txt', 'servers.dat', 'servers.dat_old')) {
            $source = Join-Path $sourceMinecraft $file
            if (Test-Path -LiteralPath $source) {
                Copy-Item -LiteralPath $source -Destination (Join-Path $testMinecraft $file) -Force
            }
        }
        $manifest = [ordered]@{
            schema = 1
            fingerprint = $fingerprint
            createdAt = (Get-Date).ToString('o')
            sourceMinecraft = $sourceMinecraft
            immutable = @('assets', 'libraries', 'versions')
        } | ConvertTo-Json -Depth 4
        Write-Utf8NoBom -Path $marker -Text $manifest
    }

    $modsPath = Join-Path $testMinecraft 'mods'
    Get-ChildItem -LiteralPath $modsPath -File -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -match '(?i)createschematicchecker.*\.jar$' } |
        ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
    Copy-Item -LiteralPath $artifact -Destination (Join-Path $modsPath (Split-Path -Leaf $artifact)) -Force
    (Get-Item -LiteralPath $instanceRoot).LastWriteTimeUtc = [DateTime]::UtcNow
    Trim-PlayerCache -InstancesPath $instancesRoot -ActiveKey $fingerprint -MaximumBytes ($CacheMaxGiB * 1GB)

    $launcherHelper = Join-Path $env:USERPROFILE '.codex\skills\pcl-mcp-client-launch\scripts\Start-PclClient.ps1'
    if (!(Test-Path -LiteralPath $launcherHelper)) {
        throw "Normal launcher bridge is unavailable: $launcherHelper"
    }
    & $launcherHelper -LatestLaunchBat $launchBat -MinecraftDir $testMinecraft -TempRoot (Join-Path $runtime 'launch') -NoLaunch:$NoLaunch
    if ($LASTEXITCODE -ne 0) { throw "Normal launcher bridge failed with exit code $LASTEXITCODE" }
    Write-Host "CSC player runtime ready: $testMinecraft"
    Write-Host "CSC cache fingerprint: $fingerprint"
} finally {
    if ($null -ne $lock) { $lock.Dispose() }
    Remove-Item -LiteralPath $lockPath -Force -ErrorAction SilentlyContinue
}
