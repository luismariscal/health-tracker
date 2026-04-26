# Changelog

All notable changes to this project should be recorded here.

## v2.39.1 - 2026-04-25

- Removed `macroPhasesCard`. It declared `<div id="macroGrid">` as a sibling of the same ID inside `macroCalcCard`, so `renderMacros()` only ever found the first match (in `macroCalcCard`) and `macroPhasesCard` rendered as a permanently empty container on every load. Cleaned up the card-description map and auto-collapse list to drop the orphaned ID.
- Synced the in-app version badge and service worker version to v2.39.1.

## v2.39.0 - 2026-04-25

- **Surface consolidation pass.** Merged `weightChartCard` + `lossChartCard` into a single `chartsCard` with a Trend / Weekly Loss toggle. Both canvases stay rendered; the toggle just switches which one is visible. Selection persists in `tp_chart_view`.
- Folded `whatIfCard` into `projectionCard`. The slider, value, and results now sit at the bottom of the 4-Week Projection card under a divider, so future scenarios live next to the current forecast instead of in a separate card further down the scroll.
- Updated the card-description map and the auto-collapse list in the workflow controller to reference the new ID (`chartsCard`) and drop the removed ones (`weightChartCard`, `lossChartCard`, `whatIfCard`).
- Synced the in-app version badge and service worker version to v2.39.0.
- Net change: **36 → 34 cards**, same information, less hunting.

## v2.38.9 - 2026-04-25

- Calmed down the injection recent log palette. Stopped applying the partial/full color class to individual chips — a logged dose isn't itself partial, only the day's overall status is, so chips now use the neutral muted style by default.
- Softened the partial date badge to muted/neutral. Only the green "All logged" badge stays colored, which keeps the at-a-glance schedule-hit signal without flooding the section with orange.
- Synced the in-app version badge and service worker version to v2.38.9.

## v2.38.8 - 2026-04-25

- Re-applied the AMOLED stealth theme as a clean inline CSS block in the head `<style>` (no PowerShell script). True-black backgrounds for OLED contrast plus subtle cyan neon glows on primary buttons, FAB, and the critical-flow card.
- Reorganized the Injection Tracker recent log: chips are now grouped by date with Today/Yesterday/date headers and an "All logged" / "N doses" badge per day. Each chip shows compound · time · site without the date prefix, replacing the flat unlabeled chip soup.
- Synced the in-app version badge and service worker version to v2.38.8.

## v2.38.7 - 2026-04-25

- **Recovery release.** Reverted `index.html`, `sw.js`, and `CHANGELOG.md` to the v2.38.2 state. The v2.38.3 AMOLED PowerShell script injected its CSS payload at every `</style>` match in the file (including inside JS template literals) and read the file with the wrong encoding, producing 1,503 mojibake sequences and broken card-content rendering. Attempts to repair in place (v2.38.4–v2.38.6) couldn't fully resolve the rendering breakage without browser diagnostics, so we rolled back to the last-known-good commit.
- **Lost in this revert (to be re-applied):** injection history grouped by date (was v2.38.4), deeper stack adherence breakdown, the CSS-class audit notes, and the AMOLED dark theme.
- If you want the AMOLED dark theme back, we'll add it as a clean inline CSS block in the head `<style>` instead of running the broken PowerShell script.
- Synced the in-app version badge and service worker version to v2.38.7.

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
