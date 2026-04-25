<#
.SYNOPSIS
    Auto-stages, commits, and pushes changes to the current Git repository.
#>

$ErrorActionPreference = "Stop"

Write-Host "Initializing Auto-Sync..." -ForegroundColor Cyan

# 1. Verify we are in a Git Repository
if (!(Test-Path ".git")) {
    Write-Host "[X] Error: Not a valid Git repository. Please run this inside your project folder." -ForegroundColor Red
    exit
}

# 2. Check for actual changes
$status = git status --porcelain
if ([string]::IsNullOrWhiteSpace($status)) {
    Write-Host "[i] No changes detected. Working tree is clean." -ForegroundColor Yellow
    exit
}

Write-Host "[+] Changes detected." -ForegroundColor Green

# 3. Get commit message
$commitMsg = Read-Host "Enter commit message (Press Enter for auto-timestamp)"

if ([string]::IsNullOrWhiteSpace($commitMsg)) {
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $commitMsg = "Auto-update: $timestamp"
}

try {
    # 4. Stage all changes (equivalent to git add -A)
    Write-Host "`n[*] Staging files..." -ForegroundColor Cyan
    git add -A

    # 5. Commit changes
    Write-Host "[*] Committing: '$commitMsg'..." -ForegroundColor Cyan
    git commit -m "$commitMsg" | Out-Null

    # 6. Push to the remote repository
    Write-Host "[*] Pushing to remote origin..." -ForegroundColor Cyan
    git push

    Write-Host "`n[✓] Sync completed successfully!" -ForegroundColor Green
}
catch {
    Write-Host "`n[X] An error occurred during the sync process:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}
