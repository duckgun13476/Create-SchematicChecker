param(
    [string]$Target = 'All',
    [ValidateSet('Compile', 'Package')]
    [string]$Phase = 'Compile',
    [string[]]$Skip = @(),
    [string]$SkipCsv = '',
    [switch]$Offline,
    [switch]$RefreshDependencies,
    [string]$LogDirectory = (Join-Path $env:TEMP 'csc-version-verification')
)

$ErrorActionPreference = 'Stop'
$root = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$rootWrapper = Join-Path $root 'gradlew.bat'
$fg3 = Join-Path $root 'gradle-runners/fg3-csc-1.14.4'
$fg5 = Join-Path $root 'gradle-runners/fg5-csc-1.16.5'

$variants = @(
    @{ Name = 'forge-1.14.4-0.2.3'; Home = $fg3; Wrapper = (Join-Path $fg3 'gradlew.bat'); Java = 8; Task = ':forge-1.14.4-0.2.3:compileJava'; Extra = @() }
    @{ Name = 'forge-1.15.2-0.3.1'; Home = $fg5; Wrapper = (Join-Path $fg5 'gradlew.bat'); Java = 17; Task = ':forge-1.15.2-0.3.1:compileJava'; Extra = @() }
    @{ Name = 'forge-1.16.5-0.3.2g'; Home = $fg5; Wrapper = (Join-Path $fg5 'gradlew.bat'); Java = 17; Task = ':forge-1.16.5-0.3.2g:compileJava'; Extra = @() }
    @{ Name = 'forge-1.17.1-0.4'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':forge-1.17.1-0.4:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'forge-1.18.2-0.5.1i'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':forge-1.18.2-0.5.1i:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'forge-1.19.2-0.5.1i'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':forge-1.19.2-0.5.1i:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'forge-1.20.1-0.5.1j'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':forge-1.20.1-0.5.1j:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'forge-1.20.1-6.0.x'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':forge-1.20.1-6.0.x:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'fabric-1.18.2-0.5.1i'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':fabric-1.18.2-0.5.1i:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'fabric-1.19.2-0.5.1i'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':fabric-1.19.2-0.5.1i:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'fabric-1.20.1-6.0.x'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':fabric-1.20.1-6.0.x:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'neoforge-1.21.1-6.0.x'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':neoforge-1.21.1-6.0.x:compileJava'; Extra = @('--configure-on-demand') }
    @{ Name = 'fabric-1.21.1-6.0.x'; Home = $root; Wrapper = $rootWrapper; Java = 21; Task = ':fabric-1.21.1-6.0.x:compileJava'; Extra = @('--configure-on-demand', '-Pcsc_verify_fabric_121=true') }
)

if (@($Skip | Where-Object { $_ -match '^@\(' }).Count -gt 0) {
    throw 'PowerShell array syntax was passed as a literal. Use -Command for -Skip @(...), or use -SkipCsv name1,name2.'
}
$skipNames = @($Skip) + @($SkipCsv.Split(',') | ForEach-Object { $_.Trim() } | Where-Object { $_ })
$selected = if ($Target -eq 'All') { @($variants | Where-Object { $_.Name -notin $skipNames }) } else { @($variants | Where-Object Name -eq $Target) }
if ($selected.Count -eq 0) {
    throw "Unknown target '$Target'. Valid targets: $($variants.Name -join ', ')"
}

$javaHomes = @{
    8 = @($env:CSC_JAVA8_HOME, 'V:\buildcache\Gradle\jdks\temurin-8-amd64-windows.2')
    17 = @($env:CSC_JAVA17_HOME, 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot')
    21 = @($env:CSC_JAVA21_HOME, 'C:\Program Files\Java\jdk-21')
}
$resolvedJava = @{}
foreach ($major in @(8, 17, 21)) {
    $resolvedJava[$major] = $javaHomes[$major] | Where-Object { $_ -and (Test-Path -LiteralPath (Join-Path $_ 'bin/java.exe')) } | Select-Object -First 1
}

$LogDirectory = [System.IO.Path]::GetFullPath($LogDirectory)
New-Item -ItemType Directory -Path $LogDirectory -Force | Out-Null
$previousJava = $env:JAVA_HOME
$previousPath = $env:PATH
$previousOnline = $env:MCSN_ALLOW_ONLINE_GRADLE
$previousGradleHome = $env:GRADLE_USER_HOME
$mirrorInit = Join-Path $root 'gradle/csc-verify-mirrors.init.gradle'
if (-not (Test-Path -LiteralPath $mirrorInit)) { throw "Missing verification mirror init script: $mirrorInit" }
$results = @()

try {
    foreach ($variant in $selected) {
        $name = $variant.Name
        $log = Join-Path $LogDirectory "$name-$($Phase.ToLowerInvariant()).log"
        if (-not $resolvedJava[$variant.Java]) {
            $results += [pscustomobject]@{ Version = $name; Result = 'MISSING_JDK'; Log = $log }
            Write-Host "[CSC verify] $name MISSING_JDK $($variant.Java)"
            continue
        }
        if (-not (Test-Path -LiteralPath $variant.Wrapper)) {
            $results += [pscustomobject]@{ Version = $name; Result = 'MISSING_WRAPPER'; Log = $log }
            Write-Host "[CSC verify] $name MISSING_WRAPPER $($variant.Wrapper)"
            continue
        }
        $env:JAVA_HOME = $resolvedJava[$variant.Java]
        $env:PATH = (Join-Path $env:JAVA_HOME 'bin') + ';' + $previousPath
        $env:MCSN_ALLOW_ONLINE_GRADLE = if ($Offline) { $null } else { 'true' }
        $env:GRADLE_USER_HOME = $previousGradleHome
        if ($variant.Java -eq 8) {
            # Gradle 4.9 has no beforeSettings hook. Keep its cached artifacts,
            # but exclude the machine-wide modern init scripts from this runner.
            if (-not $previousGradleHome) { throw 'GRADLE_USER_HOME is required for the Gradle 4.9 cache bridge.' }
            $legacyHome = Join-Path $LogDirectory 'legacy-gradle-home'
            # Windows junctions cannot reliably traverse this machine's mapped
            # V: network drive. The local Gradle cache has the same old wrapper.
            $legacyCacheSource = Join-Path $env:USERPROFILE '.gradle'
            New-Item -ItemType Directory -Path $legacyHome -Force | Out-Null
            foreach ($folder in @('caches', 'wrapper')) {
                $source = [System.IO.Path]::GetFullPath((Join-Path $legacyCacheSource $folder))
                $link = Join-Path $legacyHome $folder
                if (-not (Test-Path -LiteralPath $source)) { throw "Missing shared Gradle cache: $source" }
                if (-not (Test-Path -LiteralPath $link)) {
                    New-Item -ItemType Junction -Path $link -Target $source | Out-Null
                } elseif ((Get-Item -LiteralPath $link -Force).Target -ne $source) {
                    throw "Legacy cache bridge points to the wrong target: $link"
                }
            }
            $env:GRADLE_USER_HOME = $legacyHome
        } elseif ($name.StartsWith('fabric-')) {
            # Loom merges writable Minecraft ZIPs. On Windows, its ZIP file
            # system fails against the shared V: network cache.
            $localGradleHome = Join-Path $env:USERPROFILE '.gradle'
            if (-not (Test-Path -LiteralPath $localGradleHome)) {
                throw "Missing local Fabric Gradle cache: $localGradleHome"
            }
            $env:GRADLE_USER_HOME = $localGradleHome
        }
        $task = if ($Phase -eq 'Package') { $variant.Task -replace ':compileJava$', ':allworkJarJar' } else { $variant.Task }
        $arguments = @($task, '--console=plain', '--no-daemon', '-I', $mirrorInit) + $variant.Extra
        if ($name -eq 'forge-1.17.1-0.4') {
            # ForgeGradle has an additional hard-coded certificate fingerprint
            # probe for maven.minecraftforge.net outside Gradle repository routing.
            $arguments += '-Dnet.minecraftforge.gradle.check.certs=false'
        }
        $localMaven = 'C:\F\Codex\cache\csc-verification-maven'
        if (Test-Path -LiteralPath $localMaven) {
            $arguments += "-Pcsc_verification_maven_repo=$localMaven"
        }
        if ($Offline) { $arguments += '--offline' }
        if ($RefreshDependencies) { $arguments += '--refresh-dependencies' }

        Write-Host "[CSC verify] START $name phase=$Phase java=$($variant.Java) online=$(-not $Offline)"
        Push-Location -LiteralPath $variant.Home
        try {
            & $variant.Wrapper @arguments *> $log
            $code = $LASTEXITCODE
        } finally {
            Pop-Location
        }
        $result = if ($code -eq 0) { 'PASS' } else { "FAIL_$code" }
        $results += [pscustomobject]@{ Version = $name; Result = $result; Log = $log }
        Write-Host "[CSC verify] $result $name log=$log"
        if ($code -ne 0) {
            Get-Content -LiteralPath $log -Tail 35 | ForEach-Object { Write-Host $_ }
        }
    }
} finally {
    $env:JAVA_HOME = $previousJava
    $env:PATH = $previousPath
    $env:MCSN_ALLOW_ONLINE_GRADLE = $previousOnline
    $env:GRADLE_USER_HOME = $previousGradleHome
}

$results | Format-Table -AutoSize
if (@($results | Where-Object Result -ne 'PASS').Count -gt 0) { exit 1 }
