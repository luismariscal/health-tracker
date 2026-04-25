# Changelog

All notable changes to this project should be recorded here.

## v2.38.6 - 2026-04-25

- Completed the v2.38.3 AMOLED-encoding mojibake repair. v2.38.5 only patched the 9 most common patterns; a deeper scan exposed 615 additional corrupted sequences, including all 4-byte emoji corruptions (🫀 heart, ⚡ bolt, ⚠ warning, ⚖ scale, ⭐ star, ✓ check, ✕ cross, ▶ play, ▼ down) and 2-byte symbols (← → ↓ arrows, ≤ ≥ inequalities). Used a generalized CP1252-to-byte reverse-mapping repair instead of per-pattern strings.
- Verified DOM ID coverage: all 397 IDs from the pre-AMOLED snapshot are still present — no UI elements were lost.
- Hardened `Apply-StealthTheme.ps1` to read with explicit `-Encoding UTF8`. The original script defaulted to Windows CP1252, which is what corrupted every multi-byte UTF-8 sequence in the first place.
- Synced the in-app version badge and service worker version to v2.38.6.

## v2.38.5 - 2026-04-25

- Repaired 888 mojibake sequences introduced when the v2.38.3 PowerShell theme script read `index.html` with the wrong encoding (CP1252 instead of UTF-8) and wrote it back. Fixed em-dashes, en-dashes, ellipsis, right single quotes, bullets, middle dots, plus-minus signs, degree signs, and multiplication/division signs throughout the file.
- Synced the in-app version badge and service worker version to v2.38.5.

## v2.38.4 - 2026-04-25

- Redesigned the injection recent log: history chips are now grouped by date with a Today/Yesterday/date header row and an "All logged" or dose-count badge per day. Chips show compound · time · site without a date prefix, replacing the flat unlabeled chip soup.
- CSS audit confirmed the `water-*` block (restored in v2.38.2) was the only structural CSS casualty from v2.37.0. Remaining orphan class hits are inline-styled selector handles (`lx-*`, `power-tools-grid`, `spinner`) or inherit from global input rules (`stack-time-input`) — no additional rules needed.
- Synced the in-app version badge and service worker version to v2.38.4.

## v2.38.3 - 2026-04-25

- Injected true-black AMOLED stealth theme into the UI.
- Replaced dark blue/gray backgrounds with #000000 for infinite contrast on OLED displays.
- Added subtle cyan neon glows to primary actions and floating action buttons.

## v2.38.2 - 2026-04-25

- Restored the WATER INTAKE CSS block that was accidentally removed during the v2.37.0 trim. `water-progress`, `water-stat`, `water-quick`, `water-timeline`, `water-entry`, and the 7-day `water-trend` strip rules are back, so the hydration card stops rendering recent log entries and the Last 7 Days breakdown as bare unstyled text.
- Fixed Injection Tracker dose rows wrapping one character per line. When each compound card was narrow (the desktop grid uses `minmax(170px, 1fr)`), the row's grid layout starved the time/dose label of horizontal space because the Log button held a 132px min-width. Switched `.inj-slot-row` to a flex column so the label and full-width button always stack, matching the existing mobile treatment.
- Synced the in-app version badge and service worker version to v2.38.2.

## v2.38.1 - 2026-04-25

- Migrated five legacy `tp_injections` read sites (compareWeeks injection counting, daily review injections-today check, diagnostics counter, symptom/compound correlation analysis) to read the primary `tp_inj` keyed log via a new `flattenInjLogToList()` helper. These features were silently broken because `tp_injections` was read but never written.
- Removed an unused `tp_injections` read in `showBriefing()` that loaded the legacy array but never referenced the result.
- Added a one-shot v2.38.1 migration that purges the now-orphaned `tp_injections` localStorage key.
- Deepened the adherence coach item: below-target compounds now show their percentage and last-missed date, sorted by worst first, with overflow rolled up.
- Synced the in-app version badge and service worker version to v2.38.1.

## v2.38.0 - 2026-04-25

- Added Stack Adherence Analytics in the AI coach feed: tracks expected-vs-logged injections per compound across the last 14 days using each entry's structured `scheduleDays`/`scheduleTimes` with a ±2hr tolerance, surfacing drift (<95%), warning (<80%), and protocol-gap (<50%) tiers.
- Folded the standalone Plateau Insight Card into the primary `aiCoachCard` feed; the plateau coach item now inlines the `buildPlateauDiagnosis` culprits rather than rendering a separate card surface.
- Fixed a latent bug in `_plateauInjectionAdherence` that hardcoded a Thu/Sun-only cadence and used a broken `date:compound` key-shape check that never matched real injection logs. It now delegates to `computeStackAdherence(14)` and respects each compound's own schedule.
- Audited the Drive backup flow and confirmed the dynamic-inclusive backup pattern fully covers v2.36+ structured stack fields (`scheduleDays`, `scheduleTimes`, `route`, `category`) without any schema fix needed.
- Added a one-shot v2.38.0 migration that purges the now-orphaned `tp_plateau_snooze` localStorage key so it stops showing up in Diagnostics.
- Synced the in-app version badge and service worker version to v2.38.0.

## v2.37.1 - 2026-04-25

- Added a one-shot v2.37.0 migration that purges orphaned localStorage keys (`tp_inventory`, `tp_inventory_notify`, `tp_healthConnect`, `tp_cal_settings`, `tp_reminderGistId`, `tp_reminderToken`) on first boot so they stop showing up in Diagnostics.
- Removed dead `.voice-fab` CSS rules left behind from the v2.37.0 voice button removal (print-stylesheet hide + mobile media-query layout rule).
- Removed the `android-shell/` directory from the repo since the in-app Health Connect bridge code was severed in v2.37.0.
- Synced the in-app version badge and service worker version to v2.37.1.

## v2.37.0 - 2026-04-24

- Removed Health Connect integration end-to-end; Fitbit remains the supported wearable bridge.
- Removed the AI Quick Capture and Voice Capture surfaces in favor of the single primary AI coach panel.
- Removed the Peak Week AI card; planning lives in the main coach surface now.
- Removed the Inventory Manager (card, modal, low-stock notifications, reorder thresholds, and per-injection decrement hooks).
- Removed Calendar Export (ICS download, live Gist feed, calendar reminders modal).
- Synced the in-app version badge and service worker version to v2.37.0.

## v2.36.10 - 2026-04-24

- Refreshed the protocol compounds in Current Stack from the new dosing sheet: MOTS-c, KLOW80, Semax, HCG, weekly Retatrutide, Tesamorelin, AHK-Cu, and Selank.
- Added a one-time `tp_stack` migration so existing saved stacks pick up the new structured weekdays, times, doses, and notes for those compounds instead of staying on the older protocol.
- Left unrelated non-sheet items alone during migration so supplements and other custom rows are not silently wiped while the protocol rows are updated.
- Added first-class stack classification coverage for Semax, AHK-Cu, and Selank so they sort, route, and inventory-track correctly as injectable peptide items.

## v2.36.9 - 2026-04-24

- Added round-trip CSV import/export for Current Stack in the CSV modal. Export now covers all ten structured fields (`category`, `route`, `scheduleDays`, `scheduleTimes`) instead of the legacy six, and import replaces the stack after a confirmation.
- Kept CSV column order backward compatible: the first six columns match older exports, new columns are appended and optional on import, so existing files still open cleanly.
- Added a downloadable `stack-template.csv` so the expected format (including the `|` separator for multi-value fields) is discoverable from the UI.
- Added a repo-level `.editorconfig` so common project files default to UTF-8 and stable line endings across editors.
- Added `scripts/check-mojibake.ps1` to scan `index.html` or other target files for the most common mojibake markers before they quietly spread again.
- Synced the in-app version badge and service worker version alongside the new release so the shipped build matches the changelog.

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
