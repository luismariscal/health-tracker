param(
    [string[]]$Paths = @("index.html")
)

$patterns = @(
    ([string]([char]0x00E2) + [char]0x20AC),
    ([string][char]0x00C3),
    ([string][char]0x00C2)
)

$files = foreach ($path in $Paths) {
    if (Test-Path $path) {
        Get-Item $path
    } else {
        Write-Warning "Missing path: $path"
    }
}

if (-not $files) {
    Write-Error "No files found to scan."
    exit 1
}

$totalHits = 0

foreach ($file in $files) {
    # Review note: force UTF-8 reads here so the check itself does not hide
    # encoding regressions behind the host shell's default code page.
    $text = Get-Content -Raw -Encoding UTF8 $file.FullName
    Write-Host ""
    Write-Host "Scanning $($file.FullName)"
    foreach ($pattern in $patterns) {
        $count = ([regex]::Matches($text, [regex]::Escape($pattern))).Count
        $totalHits += $count
        "{0,-4} {1,8}" -f $pattern, $count
    }
}

Write-Host ""
if ($totalHits -gt 0) {
    Write-Error "Mojibake markers found: $totalHits"
    exit 1
}

Write-Host "No mojibake markers found."
