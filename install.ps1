[CmdletBinding()]
param(
    [Alias("v", "MCVersion", "mc")]
    [string]$Version = "latest",
    [switch]$Latest,
    [switch]$NoFabric,
    [string]$MinecraftDir = ""
)

$ErrorActionPreference = "Stop"

# Determine .minecraft directory
if ([string]::IsNullOrWhiteSpace($MinecraftDir)) {
    if ($env:APPDATA) {
        $MinecraftDir = Join-Path $env:APPDATA ".minecraft"
    } elseif ($env:HOME) {
        $MinecraftDir = Join-Path $env:HOME ".minecraft"
    } else {
        $MinecraftDir = ".minecraft"
    }
}

$ModsDir = Join-Path $MinecraftDir "mods"
if (-not (Test-Path $ModsDir)) {
    New-Item -ItemType Directory -Path $ModsDir -Force | Out-Null
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   mc-autotool Automatic Installer      " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Resolve Target Version
if ($Latest -or $Version -eq "latest" -or [string]::IsNullOrWhiteSpace($Version)) {
    Write-Host "[1/4] Resolving latest mc-autotool release..." -ForegroundColor Yellow
    try {
        $releaseInfo = Invoke-RestMethod -Uri "https://api.github.com/repos/chneau/mc-autotool/releases/latest" -Headers @{"User-Agent"="mc-autotool-installer"} -UseBasicParsing
        $TargetVersion = $releaseInfo.tag_name
        Write-Host "  -> Latest version resolved: $TargetVersion" -ForegroundColor Green
    } catch {
        $TargetVersion = "26.4"
        Write-Host "  -> Could not query GitHub API, defaulting to $TargetVersion" -ForegroundColor DarkYellow
    }
} else {
    $TargetVersion = $Version.TrimStart("v")
    Write-Host "[1/4] Selected version: $TargetVersion" -ForegroundColor Green
}

# 2. Install Fabric Profile (if needed)
if (-not $NoFabric) {
    Write-Host "[2/4] Setting up Fabric Loader profile for Minecraft $TargetVersion..." -ForegroundColor Yellow
    $javaPath = $null
    $javaCmd = Get-Command "java" -ErrorAction SilentlyContinue
    if ($javaCmd) {
        $javaPath = "java"
    } else {
        # Search common Java locations and Minecraft launcher runtime
        $foundJava = Get-ChildItem -Path "$env:LOCALAPPDATA\Packages\*\LocalCache\Local\runtime", "$env:APPDATA\.minecraft\runtime", "C:\Program Files (x86)\Minecraft Launcher\runtime", "C:\Program Files\Java", "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Microsoft" -Filter "java.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName
        if ($foundJava) {
            $javaPath = $foundJava
            Write-Host "  -> Found Minecraft/system Java: $javaPath" -ForegroundColor Gray
        }
    }

    if (-not $javaPath) {
        Write-Warning "Java was not found. Skipping automatic Fabric profile installation."
        Write-Warning "Please install Fabric manually from https://fabricmc.net/use/installer/"
    } else {
        try {
            $installerList = Invoke-RestMethod -Uri "https://meta.fabricmc.net/v2/versions/installer" -UseBasicParsing
            $installerUrl = ($installerList | Where-Object { $_.stable -eq $true } | Select-Object -First 1).url
            if (-not $installerUrl) {
                $installerUrl = $installerList[0].url
            }

            $tempInstaller = Join-Path ([System.IO.Path]::GetTempPath()) "fabric-installer.jar"
            Write-Host "  -> Downloading Fabric Installer..." -ForegroundColor Gray
            Invoke-WebRequest -Uri $installerUrl -OutFile $tempInstaller -UseBasicParsing

            # Determine mcversion argument (support snapshots e.g. 26.4-snapshot-1)
            $mcVerArg = $TargetVersion
            if ($TargetVersion -eq "26.4") { $mcVerArg = "26.4-snapshot-1" }

            Write-Host "  -> Running Fabric Installer for Minecraft $mcVerArg..." -ForegroundColor Gray
            $process = Start-Process -FilePath $javaPath -ArgumentList "-jar", "`"$tempInstaller`"", "client", "-mcversion", "$mcVerArg", "-dir", "`"$MinecraftDir`"" -NoNewWindow -Wait -PassThru

            if ($process.ExitCode -eq 0) {
                Write-Host "  -> Fabric profile successfully installed!" -ForegroundColor Green
            } else {
                Write-Warning "Fabric installer exited with code $($process.ExitCode). You may need to run the installer manually."
            }
            Remove-Item -Path $tempInstaller -Force -ErrorAction SilentlyContinue
        } catch {
            Write-Warning "Failed to run Fabric installer automatically: $_"
        }
    }
} else {
    Write-Host "[2/4] Skipping Fabric profile installation (-NoFabric specified)." -ForegroundColor Gray
}

# 3. Download Fabric API
Write-Host "[3/4] Fetching compatible Fabric API..." -ForegroundColor Yellow
$fapiUrl = $null
$fapiVersionStr = ""

# Try Modrinth exact match
try {
    $modrinthApi = "https://api.modrinth.com/v2/project/fabric-api/version?game_versions=%5B%22$TargetVersion%22%5D&loaders=%5B%22fabric%22%5D"
    $fapiVersions = Invoke-RestMethod -Uri $modrinthApi -Headers @{"User-Agent"="mc-autotool-installer"} -UseBasicParsing
    if ($fapiVersions -and $fapiVersions.Count -gt 0) {
        $primaryFile = ($fapiVersions[0].files | Where-Object { $_.primary -eq $true } | Select-Object -First 1)
        if (-not $primaryFile) { $primaryFile = $fapiVersions[0].files[0] }
        $fapiUrl = $primaryFile.url
        $fapiVersionStr = $fapiVersions[0].version_number
    }
} catch {}

# Fallback 1: Search Modrinth for matching version
if (-not $fapiUrl) {
    try {
        $modrinthApi = "https://api.modrinth.com/v2/project/fabric-api/version?loaders=%5B%22fabric%22%5D"
        $fapiVersions = Invoke-RestMethod -Uri $modrinthApi -Headers @{"User-Agent"="mc-autotool-installer"} -UseBasicParsing
        $matchedVersion = $fapiVersions | Where-Object { ($_.game_versions -contains $TargetVersion) -or ($_.version_number -like "*+$TargetVersion*") -or ($_.game_versions -contains "$TargetVersion-snapshot-1") } | Select-Object -First 1
        if ($matchedVersion) {
            $primaryFile = ($matchedVersion.files | Where-Object { $_.primary -eq $true } | Select-Object -First 1)
            if (-not $primaryFile) { $primaryFile = $matchedVersion.files[0] }
            $fapiUrl = $primaryFile.url
            $fapiVersionStr = $matchedVersion.version_number
        }
    } catch {}
}

# Fallback 2: Check Maven repository metadata
if (-not $fapiUrl) {
    try {
        $cleanVer = $TargetVersion.Split('-')[0]
        $mavenUrl = "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml"
        $metaContent = (Invoke-WebRequest -Uri $mavenUrl -UseBasicParsing).Content
        [xml]$mavenXml = $metaContent
        $versionNodes = @($mavenXml.metadata.versioning.versions.version) | Where-Object { $_ -like "*+$TargetVersion*" -or $_ -like "*+$cleanVer*" }
        if ($versionNodes -and $versionNodes.Count -gt 0) {
            $latestMavenVer = $versionNodes[-1]
            $fapiUrl = "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/$latestMavenVer/fabric-api-$latestMavenVer.jar"
            $fapiVersionStr = $latestMavenVer
        }
    } catch {}
}

if ($fapiUrl) {
    $fapiDest = Join-Path $ModsDir "fabric-api.jar"
    Write-Host "  -> Downloading Fabric API ($fapiVersionStr)..." -ForegroundColor Gray
    Invoke-WebRequest -Uri $fapiUrl -OutFile $fapiDest -UseBasicParsing
    Write-Host "  -> Fabric API installed to $fapiDest" -ForegroundColor Green
} else {
    Write-Warning "Could not find Fabric API build for Minecraft $TargetVersion."
}

# 4. Download mc-autotool
Write-Host "[4/4] Downloading Autotool for Minecraft $TargetVersion..." -ForegroundColor Yellow
$autotoolUrl = if ($TargetVersion -eq "latest" -or ($releaseInfo -and $releaseInfo.tag_name -eq $TargetVersion)) {
    "https://github.com/chneau/mc-autotool/releases/latest/download/autotool.jar"
} else {
    "https://github.com/chneau/mc-autotool/releases/download/$TargetVersion/autotool.jar"
}

$autotoolDest = Join-Path $ModsDir "autotool.jar"
try {
    Invoke-WebRequest -Uri $autotoolUrl -OutFile $autotoolDest -UseBasicParsing
    Write-Host "  -> Autotool installed to $autotoolDest" -ForegroundColor Green
} catch {
    Write-Warning "Failed to download autotool from $($autotoolUrl): $_"
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Installation Complete!" -ForegroundColor Green
Write-Host " 1. Open Minecraft Launcher" -ForegroundColor White
Write-Host " 2. Select the Fabric profile for $TargetVersion" -ForegroundColor White
Write-Host " 3. Launch the game" -ForegroundColor White
Write-Host " Press 'Ctrl + Shift + O' or type '/autotool' in-game to configure Autotool." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
