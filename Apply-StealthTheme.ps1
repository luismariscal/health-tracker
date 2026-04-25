<#
.SYNOPSIS
    Injects the AMOLED Stealth Tech CSS overrides into index.html.
#>

$htmlFile = ".\index.html"
$backupFile = ".\index.backup_theme.html"

Write-Host "Initializing Theme Injection..." -ForegroundColor Cyan

# 1. Verify index.html exists
if (!(Test-Path $htmlFile)) {
    Write-Host "[X] Error: index.html not found in the current directory." -ForegroundColor Red
    exit
}

# 2. Create a backup before modifying
Copy-Item -Path $htmlFile -Destination $backupFile -Force
Write-Host "[+] Created backup: $backupFile" -ForegroundColor Green

# 3. Read the HTML content
$content = Get-Content -Path $htmlFile -Raw -Encoding UTF8

# 4. Prevent double-injection
if ($content -match "/\* --- INJECTED AMOLED THEME --- \*/") {
    Write-Host "[!] The AMOLED theme has already been injected. Aborting to prevent duplicates." -ForegroundColor Yellow
    exit
}

# 5. Define the CSS Payload (Using an array to avoid whitespace parsing errors)
$amoledCss = [string]::Join("`r`n", @(
    "/* --- INJECTED AMOLED THEME --- */"
    ":root {"
    "    --bg: #000000;"
    "    --surface: #080a0f;"
    "    --surface2: #0f131c;"
    "    --border: #1a202c;"
    "    --accent: #00d4ff;"
    "    --accent2: #7c3aed;"
    "    --green: #10b981;"
    "    --yellow: #f59e0b;"
    "    --red: #ef4444;"
    "    --orange: #f97316;"
    "    --text: #f8fafc;"
    "    --muted: #64748b;"
    "    --card-radius: 12px;"
    "}"
    ""
    "html, body {"
    "    background: var(--bg) !important;"
    "    color: var(--text) !important;"
    "    scrollbar-color: var(--border) var(--bg);"
    "    scrollbar-width: thin;"
    "}"
    ""
    ".topbar {"
    "    background: rgba(8, 10, 15, 0.85) !important;"
    "    backdrop-filter: blur(12px);"
    "    -webkit-backdrop-filter: blur(12px);"
    "}"
    ""
    ".btn-primary {"
    "    box-shadow: 0 0 12px rgba(0, 212, 255, 0.15) !important;"
    "}"
    ""
    ".btn-primary:hover {"
    "    box-shadow: 0 0 20px rgba(0, 212, 255, 0.35) !important;"
    "}"
    ""
    ".fab {"
    "    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.7), 0 0 0 2px rgba(0, 212, 255, 0.15), 0 0 15px rgba(0, 212, 255, 0.25) !important;"
    "}"
    ""
    ".critical-flow-card {"
    "    border-color: rgba(0, 212, 255, 0.35) !important;"
    "    background: linear-gradient(135deg, rgba(0, 212, 255, 0.04), transparent 40%), var(--surface) !important;"
    "    box-shadow: inset 0 0 24px rgba(0, 212, 255, 0.03) !important;"
    "}"
    "/* ----------------------------- */"
    "</style>"
))

# 6. Inject the payload right before the closing </style> tag
$newContent = $content -replace "(?i)</style>", $amoledCss

# 7. Write the updated content back to index.html
Set-Content -Path $htmlFile -Value $newContent -Encoding UTF8

Write-Host "`n[✓] AMOLED CSS successfully injected into index.html!" -ForegroundColor Green