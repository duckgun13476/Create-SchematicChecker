param(
    [Parameter(Mandatory = $true)][string]$ArtifactPath,
    [Parameter(Mandatory = $true)][string]$MinecraftVersion,
    [string]$PrismRoot = (Join-Path $env:APPDATA 'PrismLauncher'),
    [string]$PrismExecutable = (Join-Path $env:LOCALAPPDATA 'Programs\PrismLauncher\prismlauncher.exe'),
    [string]$BaseInstance,
    [string]$FabricApiJar,
    [string]$FabricLoaderVersion = '0.17.2',
    [string]$RuntimeRoot = (Join-Path $env:LOCALAPPDATA 'CreateSchematicChecker\PrismRuntime'),
    [ValidateRange(1, 512)][int]$CacheMaxGiB = 12,
    [switch]$UsePlayerAccount,
    [switch]$NoLaunch
)

$ErrorActionPreference = 'Stop'

function Write-Utf8NoBom {
    param([string]$Path, [string]$Text)
    [System.IO.File]::WriteAllText($Path, $Text, [System.Text.UTF8Encoding]::new($false))
}

function Get-InputFingerprint {
    param([string]$InstanceRoot, [string]$TargetFabricLoaderVersion)

    $minecraft = Join-Path $InstanceRoot 'minecraft'
    $hash = [System.Security.Cryptography.IncrementalHash]::CreateHash([System.Security.Cryptography.HashAlgorithmName]::SHA256)
    $encoding = [System.Text.Encoding]::UTF8
    $add = { param([string]$value) $hash.AppendData($encoding.GetBytes($value + "`n")) }
    & $add 'csc-prism-runtime-v2'
    & $add "fabric-loader=$TargetFabricLoaderVersion"
    foreach ($root in @('mods', 'config', 'defaultconfigs', 'kubejs', 'resourcepacks', 'shaderpacks', 'datapacks')) {
        $path = Join-Path $minecraft $root
        if (!(Test-Path -LiteralPath $path)) { continue }
        $files = if ($root -eq 'mods') {
            Get-ChildItem -LiteralPath $path -File -Recurse | Where-Object { $_.Name -match '(?i)\.jar(?:\.disabled)?$' }
        } else {
            Get-ChildItem -LiteralPath $path -File -Recurse
        }
        $files | Sort-Object FullName | ForEach-Object {
            & $add $_.FullName.Substring($minecraft.Length).TrimStart('\\')
            & $add $_.Length.ToString()
            & $add (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash
        }
    }
    foreach ($file in @('instance.cfg', 'mmc-pack.json')) {
        $path = Join-Path $InstanceRoot $file
        if (Test-Path -LiteralPath $path) { & $add $file; & $add (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash }
    }
    return ([Convert]::ToHexString($hash.GetHashAndReset())).ToLowerInvariant()
}

function Copy-ModJars {
    param([string]$Source, [string]$Destination)
    New-Item -ItemType Directory -Force -Path $Destination | Out-Null
    Get-ChildItem -LiteralPath $Source -File -Recurse |
        Where-Object { $_.Name -match '(?i)\.jar(?:\.disabled)?$' } |
        ForEach-Object {
            if ($_.Name -match '(?i)createschematicchecker.*\.jar$') { return }
            $target = Join-Path $Destination $_.FullName.Substring($Source.Length).TrimStart('\\')
            New-Item -ItemType Directory -Force -Path (Split-Path -Parent $target) | Out-Null
            try { New-Item -ItemType HardLink -Path $target -Target $_.FullName -ErrorAction Stop | Out-Null }
            catch { Copy-Item -LiteralPath $_.FullName -Destination $target -Force }
        }
}

function New-ReadOnlyRuntimeLink {
    param([string]$Path, [string]$Target)
    if (Test-Path -LiteralPath $Path) { return }
    if (!(Test-Path -LiteralPath $Target)) { return }
    New-Item -ItemType Junction -Path $Path -Target $Target | Out-Null
}

function Initialize-PrismRuntime {
    param([string]$Path, [string]$PlayerRoot, [bool]$AllowPlayerAccount)
    New-Item -ItemType Directory -Force -Path $Path | Out-Null
    # A separate Prism data root avoids IPC to an already-running launcher,
    # which otherwise retains an in-memory instance list and misses a freshly
    # generated regression instance.  Large launcher-managed assets stay shared.
    foreach ($directory in @('assets', 'libraries', 'java', 'meta', 'translations')) {
        New-ReadOnlyRuntimeLink -Path (Join-Path $Path $directory) -Target (Join-Path $PlayerRoot $directory)
    }
    New-Item -ItemType Directory -Force -Path (Join-Path $Path 'instances') | Out-Null
    $configPath = Join-Path $Path 'prismlauncher.cfg'
    # Prism's quick-setup completion is stored in its normal configuration.
    # Keep launcher/runtime choices consistent with the user's client but never
    # copy account data or third-party tokens into the test root.
    $sourceConfig = Join-Path $PlayerRoot 'prismlauncher.cfg'
    $configText = Get-Content -LiteralPath $sourceConfig -Raw
    $configText = [regex]::Replace($configText, '(?m)^ModrinthToken=.*$', 'ModrinthToken=')
    $configText = [regex]::Replace($configText, '(?m)^MSAClientIDOverride=.*$', 'MSAClientIDOverride=')
    $configText = [regex]::Replace($configText, '(?m)^Env=.*$', 'Env={}')
    $configText = [regex]::Replace($configText, '(?m)^SelectedInstance=.*$', 'SelectedInstance=')
    if ($configText -notmatch '(?m)^CSCRegressionRuntime=') { $configText += "`r`nCSCRegressionRuntime=true`r`n" }
    Write-Utf8NoBom -Path $configPath -Text $configText

    # Prism 11 opens onboarding whenever no MSA profile with Minecraft ownership
    # exists, even when the CLI will launch offline. A token-free profile still
    # triggers its account refresher, so a real client launch needs an isolated
    # full account copy. Never do that without an explicit opt-in.
    $accountsPath = Join-Path $Path 'accounts.json'
    if (!$AllowPlayerAccount) {
        throw 'Prism requires its local account record before it will start a headless instance. Re-run with -UsePlayerAccount only after approving a private local account copy; no project file is used for that copy.'
    }
    $sourceAccountsPath = Join-Path $PlayerRoot 'accounts.json'
    $sourceAccounts = Get-Content -LiteralPath $sourceAccountsPath -Raw | ConvertFrom-Json
    $sourceAccount = @($sourceAccounts.accounts | Where-Object {
        $_.type -eq 'MSA' -and $_.entitlement.ownsMinecraft -eq $true -and $_.profile.id -and $_.profile.name
    } | Sort-Object { if ($_.active) { 0 } else { 1 } } | Select-Object -First 1)
    if ($sourceAccount.Count -ne 1) { throw 'Prism regression launch needs one existing local MSA account with Minecraft ownership; no credential data will be copied.' }
    $accountCopy = $sourceAccount[0] | ConvertTo-Json -Depth 32 | ConvertFrom-Json
    $accountCopy.active = $true
    Write-Utf8NoBom -Path $accountsPath -Text ([ordered]@{ formatVersion = 3; accounts = @($accountCopy) } | ConvertTo-Json -Depth 32)
}

function Resolve-Java21 {
    $candidates = @(
        if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\javaw.exe' }
        'C:\Program Files\Java\jdk-21\bin\javaw.exe'
        'C:\Program Files\Java\jdk-21\bin\java.exe'
    ) | Where-Object { $_ -and (Test-Path -LiteralPath $_) }
    foreach ($candidate in $candidates) {
        $java = if ((Split-Path -Leaf $candidate) -ieq 'javaw.exe') { Join-Path (Split-Path -Parent $candidate) 'java.exe' } else { $candidate }
        $version = (& $java -version 2>&1 | Select-Object -First 1) -join "`n"
        if ($version -match 'version "(?:21|2[2-9])(?:\.|"|\+)') { return (Resolve-Path -LiteralPath $candidate).Path }
    }
    throw 'Create 6.0.8.1 requires Java 21, but no usable Java 21 runtime was found. Set JAVA_HOME to a Java 21 installation before running the Prism regression.'
}

function Get-DirectoryBytes {
    param([string]$Path)
    [int64]$total = 0
    $pending = [System.Collections.Generic.Stack[string]]::new()
    $pending.Push($Path)
    while ($pending.Count -gt 0) {
        foreach ($entry in @(Get-ChildItem -LiteralPath $pending.Pop() -Force -ErrorAction SilentlyContinue)) {
            if ($entry.PSIsContainer) {
                if (($entry.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -eq 0) { $pending.Push($entry.FullName) }
            } else { $total += [int64]$entry.Length }
        }
    }
    return $total
}

function Trim-CscPrismInstances {
    param([string]$InstancesRoot, [string]$ActiveName, [int64]$MaximumBytes)
    $instances = @(Get-ChildItem -LiteralPath $InstancesRoot -Directory -Force |
        Where-Object { $_.Name -like 'CSC-Test-*' } |
        ForEach-Object { [pscustomobject]@{ Item = $_; Bytes = Get-DirectoryBytes $_.FullName } })
    [int64]$total = ($instances | Measure-Object -Property Bytes -Sum).Sum
    $parent = (Resolve-Path -LiteralPath $InstancesRoot).Path.TrimEnd('\\') + '\\'
    foreach ($entry in ($instances | Where-Object { $_.Item.Name -ne $ActiveName } | Sort-Object { $_.Item.LastWriteTimeUtc })) {
        if ($total -le $MaximumBytes) { break }
        $resolved = (Resolve-Path -LiteralPath $entry.Item.FullName).Path
        if (!$resolved.StartsWith($parent, [System.StringComparison]::OrdinalIgnoreCase) -or $entry.Item.Name -notlike 'CSC-Test-*') {
            throw "Refusing Prism test-cache eviction outside the CSC namespace: $resolved"
        }
        Remove-Item -LiteralPath $resolved -Recurse -Force
        $total -= $entry.Bytes
    }
}

if (!(Test-Path -LiteralPath $ArtifactPath)) { throw "CSC artifact does not exist: $ArtifactPath" }
if (!(Test-Path -LiteralPath $PrismRoot)) { throw "Prism Launcher data root does not exist: $PrismRoot" }
if (!(Test-Path -LiteralPath $PrismExecutable)) { throw "Prism Launcher executable does not exist: $PrismExecutable" }
$artifact = (Resolve-Path -LiteralPath $ArtifactPath).Path
$root = (Resolve-Path -LiteralPath $PrismRoot).Path
$sourceInstancesRoot = Join-Path $root 'instances'
if (!(Test-Path -LiteralPath $sourceInstancesRoot)) { throw "Prism instances directory does not exist: $sourceInstancesRoot" }
Initialize-PrismRuntime -Path $RuntimeRoot -PlayerRoot $root -AllowPlayerAccount $UsePlayerAccount.IsPresent
$runtime = (Resolve-Path -LiteralPath $RuntimeRoot).Path
$instancesRoot = Join-Path $runtime 'instances'
$java21 = Resolve-Java21

$describeCreateBase = {
    param([System.IO.DirectoryInfo]$candidate)
    $packPath = Join-Path $candidate.FullName 'mmc-pack.json'
    $modsPath = Join-Path $candidate.FullName 'minecraft\mods'
    if (!(Test-Path -LiteralPath $packPath) -or !(Test-Path -LiteralPath $modsPath)) { return $null }
    try { $pack = Get-Content -LiteralPath $packPath -Raw | ConvertFrom-Json } catch { return $null }
    $hasVersion = @($pack.components | Where-Object { $_.uid -eq 'net.minecraft' -and $_.version -eq $MinecraftVersion }).Count -eq 1
    $hasFabric = @($pack.components | Where-Object { $_.uid -eq 'net.fabricmc.fabric-loader' }).Count -eq 1
    $hasQuilt = @($pack.components | Where-Object { $_.uid -eq 'org.quiltmc.quilt-loader' }).Count -eq 1
    $hasCreate = @(Get-ChildItem -LiteralPath $modsPath -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -match '(?i)^create-fabric-.*\.jar$' }).Count -ge 1
    if (!$hasVersion -or !$hasCreate) { return $null }
    if ($hasFabric) { return 'fabric' }
    if ($hasQuilt) { return 'quilt' }
    return $null
}

if (![string]::IsNullOrWhiteSpace($BaseInstance)) {
    $candidate = Get-Item -LiteralPath (Join-Path $sourceInstancesRoot $BaseInstance) -ErrorAction Stop
    $baseMode = if ($candidate.PSIsContainer) { & $describeCreateBase $candidate } else { $null }
    if ($null -eq $baseMode) {
        throw "The selected Prism base instance is not a $MinecraftVersion Create instance with Fabric or Quilt loader: $BaseInstance"
    }
    $base = $candidate
} else {
    $base = @(Get-ChildItem -LiteralPath $sourceInstancesRoot -Directory -Force |
        Where-Object { $_.Name -notlike 'CSC-Test-*' -and $null -ne (& $describeCreateBase $_) } |
        Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1)
    if ($base.Count -eq 0) {
        throw "No Prism base instance matches $MinecraftVersion + Create with Fabric or Quilt loader. Create/import that normal player instance once, then runClient will select it automatically."
    }
    $base = $base[0]
    $baseMode = & $describeCreateBase $base
}

if ($baseMode -eq 'quilt') {
    if ([string]::IsNullOrWhiteSpace($FabricApiJar) -or !(Test-Path -LiteralPath $FabricApiJar)) {
        throw "A Quilt base requires the resolved Fabric API jar to build the isolated Fabric test instance: $FabricApiJar"
    }
    $FabricApiJar = (Resolve-Path -LiteralPath $FabricApiJar).Path
}

$fingerprint = Get-InputFingerprint -InstanceRoot $base.FullName -TargetFabricLoaderVersion $FabricLoaderVersion
$instanceName = "CSC-Test-$MinecraftVersion-$($fingerprint.Substring(0, 16))"
$instanceRoot = Join-Path $instancesRoot $instanceName
$marker = Join-Path $instanceRoot 'runtime-manifest.json'
$testMinecraft = Join-Path $instanceRoot 'minecraft'
if (!(Test-Path -LiteralPath $marker)) {
    New-Item -ItemType Directory -Force -Path $testMinecraft | Out-Null
    foreach ($file in @('instance.cfg', 'mmc-pack.json')) {
        Copy-Item -LiteralPath (Join-Path $base.FullName $file) -Destination (Join-Path $instanceRoot $file) -Force
    }
    $cfg = Join-Path $instanceRoot 'instance.cfg'
    $cfgText = Get-Content -LiteralPath $cfg -Raw
    $cfgText = [regex]::Replace($cfgText, '(?m)^name=.*$', "name=CSC Fabric $MinecraftVersion test")
    $javaPathForConfig = $java21.Replace('\', '/')
    $cfgText = [regex]::Replace($cfgText, '(?m)^AutomaticJava=.*$', 'AutomaticJava=false')
    $cfgText = [regex]::Replace($cfgText, '(?m)^OverrideJavaLocation=.*$', 'OverrideJavaLocation=true')
    $cfgText = [regex]::Replace($cfgText, '(?m)^IgnoreJavaCompatibility=.*$', 'IgnoreJavaCompatibility=true')
    $cfgText = [regex]::Replace($cfgText, '(?m)^JavaPath=.*$', "JavaPath=$javaPathForConfig")
    $cfgText = [regex]::Replace($cfgText, '(?m)^JavaVersion=.*$', 'JavaVersion=21')
    Write-Utf8NoBom -Path $cfg -Text $cfgText
    if ($baseMode -eq 'quilt') {
        $packPath = Join-Path $instanceRoot 'mmc-pack.json'
        $pack = Get-Content -LiteralPath $packPath -Raw | ConvertFrom-Json
        $pack.components = @($pack.components | Where-Object { $_.uid -ne 'org.quiltmc.quilt-loader' })
        $pack.components += [pscustomobject]@{
            cachedName = 'Fabric Loader'
            cachedRequires = @([pscustomobject]@{ uid = 'net.fabricmc.intermediary' })
            cachedVersion = $FabricLoaderVersion
            uid = 'net.fabricmc.fabric-loader'
            version = $FabricLoaderVersion
        }
        Write-Utf8NoBom -Path $packPath -Text ($pack | ConvertTo-Json -Depth 16)
    }
    foreach ($directory in @('mods', 'config', 'defaultconfigs', 'kubejs', 'resourcepacks', 'shaderpacks', 'datapacks')) {
        $source = Join-Path $base.FullName "minecraft\\$directory"
        if (!(Test-Path -LiteralPath $source)) { continue }
        $destination = Join-Path $testMinecraft $directory
        if ($directory -eq 'mods') { Copy-ModJars -Source $source -Destination $destination }
        else { Copy-Item -LiteralPath $source -Destination $destination -Recurse -Force }
    }
    if ($baseMode -eq 'quilt') {
        $mods = Join-Path $testMinecraft 'mods'
        Get-ChildItem -LiteralPath $mods -File -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match '(?i)^(?:qfapi|fabric-api)-.*\.jar$' } |
            ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
        Copy-Item -LiteralPath $FabricApiJar -Destination (Join-Path $mods (Split-Path -Leaf $FabricApiJar)) -Force
    }
    foreach ($file in @('options.txt', 'optionsof.txt', 'servers.dat', 'servers.dat_old')) {
        $source = Join-Path $base.FullName "minecraft\\$file"
        if (Test-Path -LiteralPath $source) { Copy-Item -LiteralPath $source -Destination (Join-Path $testMinecraft $file) -Force }
    }
    Write-Utf8NoBom -Path $marker -Text ([ordered]@{ schema = 2; baseInstance = $base.Name; baseLoader = $baseMode; fingerprint = $fingerprint; createdAt = (Get-Date).ToString('o') } | ConvertTo-Json)
}

# Reapply the Java requirement on reused cache layers; older generated layers
# may have inherited a Java 17 override from their player base instance.
$cfg = Join-Path $instanceRoot 'instance.cfg'
$cfgText = Get-Content -LiteralPath $cfg -Raw
$javaPathForConfig = $java21.Replace('\', '/')
$cfgText = [regex]::Replace($cfgText, '(?m)^AutomaticJava=.*$', 'AutomaticJava=false')
$cfgText = [regex]::Replace($cfgText, '(?m)^OverrideJavaLocation=.*$', 'OverrideJavaLocation=true')
$cfgText = [regex]::Replace($cfgText, '(?m)^IgnoreJavaCompatibility=.*$', 'IgnoreJavaCompatibility=true')
$cfgText = [regex]::Replace($cfgText, '(?m)^JavaPath=.*$', "JavaPath=$javaPathForConfig")
$cfgText = [regex]::Replace($cfgText, '(?m)^JavaVersion=.*$', 'JavaVersion=21')
Write-Utf8NoBom -Path $cfg -Text $cfgText

$mods = Join-Path $testMinecraft 'mods'
Get-ChildItem -LiteralPath $mods -File -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -match '(?i)^createschematicchecker.*\.jar$' } |
    ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
Copy-Item -LiteralPath $artifact -Destination (Join-Path $mods (Split-Path -Leaf $artifact)) -Force
(Get-Item -LiteralPath $instanceRoot).LastWriteTimeUtc = [DateTime]::UtcNow
Trim-CscPrismInstances -InstancesRoot $instancesRoot -ActiveName $instanceName -MaximumBytes ($CacheMaxGiB * 1GB)

Write-Host "CSC Prism runtime ready: $instanceName (base=$($base.Name), sourceLoader=$baseMode, fingerprint=$fingerprint, runtime=$runtime)"
if (!$NoLaunch) {
    # Prism itself resolved the one-time component download through the normal
    # path. Afterwards use a fixed offline identity so an unattended regression
    # cannot stall on the launcher player-name dialog.
    Start-Process -FilePath $PrismExecutable -ArgumentList @('-d', $runtime, '-l', $instanceName, '-o', 'CSCRegression') -WorkingDirectory (Split-Path -Parent $PrismExecutable)
}
