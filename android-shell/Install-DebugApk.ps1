param(
    [switch]$OpenFolderOnly
)

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$apkDir = Join-Path $projectRoot "app\build\outputs\apk\debug"

if (-not (Test-Path $apkDir)) {
    Write-Host ""
    Write-Host "No debug APK folder exists yet." -ForegroundColor Yellow
    Write-Host "Build the Android shell first in Android Studio." -ForegroundColor Yellow
    Write-Host "Expected output folder:" -ForegroundColor DarkGray
    Write-Host "  $apkDir" -ForegroundColor Cyan
    exit 1
}

$apk = Get-ChildItem -Path $apkDir -Filter *.apk |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $apk) {
    Write-Host ""
    Write-Host "No APK found in the debug output folder yet." -ForegroundColor Yellow
    Write-Host "Build the app in Android Studio, then rerun this script." -ForegroundColor Yellow
    exit 1
}

if ($OpenFolderOnly) {
    Start-Process explorer.exe $apkDir
    exit 0
}

$adb = Get-Command adb -ErrorAction SilentlyContinue

if (-not $adb) {
    Write-Host ""
    Write-Host "adb was not found on PATH." -ForegroundColor Yellow
    Write-Host "Opening the APK folder instead so you can install it manually." -ForegroundColor Yellow
    Write-Host "Latest APK:" -ForegroundColor DarkGray
    Write-Host "  $($apk.FullName)" -ForegroundColor Cyan
    Start-Process explorer.exe $apkDir
    exit 1
}

Write-Host "Installing latest debug APK..." -ForegroundColor Green
& $adb.Source install -r $apk.FullName
