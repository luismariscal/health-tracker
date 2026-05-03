param(
    [string]$StudioPath
)

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

# Review note: keep the launcher dead simple so the user can open the shell
# project without remembering the folder path every time.
$candidates = @(
    $StudioPath,
    $env:ANDROID_STUDIO_EXE,
    "C:\Program Files\Android\Android Studio\bin\studio64.exe",
    "C:\Program Files\Android\Android Studio\bin\studio.exe",
    "$env:LOCALAPPDATA\Programs\Android Studio\bin\studio64.exe",
    "$env:LOCALAPPDATA\Programs\Android Studio\bin\studio.exe"
) | Where-Object { $_ -and (Test-Path $_) }

$resolvedStudio = $candidates | Select-Object -First 1

if (-not $resolvedStudio) {
    Write-Host ""
    Write-Host "Android Studio was not found automatically." -ForegroundColor Yellow
    Write-Host "Open this folder manually in Android Studio:" -ForegroundColor Yellow
    Write-Host "  $projectRoot" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Optional: rerun with -StudioPath 'C:\Path\To\studio64.exe'" -ForegroundColor DarkGray
    exit 1
}

Write-Host "Opening Android shell in Android Studio..." -ForegroundColor Green
Start-Process -FilePath $resolvedStudio -ArgumentList "`"$projectRoot`""
