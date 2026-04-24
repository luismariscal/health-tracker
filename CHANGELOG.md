# Changelog

All notable changes to this project should be recorded here.

## Unreleased

- Added a repo-level `.editorconfig` so common project files default to UTF-8 and stable line endings across editors.
- Added `scripts/check-mojibake.ps1` to scan `index.html` or other target files for the most common mojibake markers before they quietly spread again.

## v2.36.8 - 2026-04-24

- Repaired accumulated mojibake throughout index.html: 1,421 lines cleaned, restoring em/en-dashes, middle dots, arrows, math symbols, check/cross marks, and all emoji (injection, meal, lab, etc.) that had been corrupted through multiple save/encode cycles.
- Restored the numeric-range regex to correctly match hyphen, "to", em-dash, and en-dash.
- Removed ~1.5 MB of encoding garbage (file shrank 3.4 MB → 2.0 MB) with no change to line count or structure.

## v2.36.7 - 2026-04-23

- Fixed Injection Tracker readability in narrow mobile and tablet layouts.
- Separated mobile collapsed-card state from desktop collapsed-card state.
- Tightened split-dose parsing so only real morning/evening dose text is split across dose slots.

## v2.36.6 - 2026-04-23

- Refocused the mobile Today experience around a clearer top-to-bottom logging flow.
- Simplified the phone Injection Tracker into a more readable action-first layout.
- Reduced mobile floating control clutter and compacted Health Connect and Fitbit status text.

## v2.36.5 - 2026-04-23

- Scaffolded an Android shell for Health Connect integration.
- Added the first native bridge path for weight, steps, sleep, and resting heart rate sync.

## v2.36.4 - 2026-04-23

- Cleaned legacy injection schedules so extra phantom dose times stop appearing.
- Added a direct Edit schedule path from Injection Tracker into Current Stack.

## v2.36.3 - 2026-04-23

- Moved the daily workflow to the top of the page.
- Added a stronger single-summary plus daily-input flow for logging.
- Improved split-dose injection logging and editing behavior.

## v2.36.2 - 2026-04-23

- Cleaned notification and reminder copy to reduce mojibake in user-facing alerts.

## v2.36.1 - 2026-04-23

- Improved Fitbit integration with broader sync coverage and stale-data refresh behavior.

## v2.36.0 - 2026-04-23

- Added structured weekday scheduling in Current Stack.
- Added split-dose slot tracking in Injection Tracker for compounds with multiple daily doses.
