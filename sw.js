// =========================================================
// Health Tracker — Service Worker
// Handles notification focus, periodic background sync for
// hydration + injection reminders, and basic cache shell.
// Served from GitHub Pages at /health-tracker/sw.js.
// =========================================================
const SW_VERSION = 'tp-sw-v2.31.5';
const SCOPE = self.registration.scope;

self.addEventListener('install', (e) => {
  // Activate the new worker immediately so updates roll out
  self.skipWaiting();
});

self.addEventListener('activate', (e) => {
  e.waitUntil((async () => {
    // Review note: clear older app-shell caches when the SW version changes so
    // installed mobile PWAs do not keep serving stale Health Tracker builds.
    try {
      const keys = await caches.keys();
      await Promise.all(
        keys
          .filter((key) => key.startsWith('tp-sw-') && key !== SW_VERSION)
          .map((key) => caches.delete(key))
      );
    } catch { /* cache cleanup is best effort */ }
    await self.clients.claim();
  })());
});

// Network-first (default) — no caching yet; future-proofed hook.
self.addEventListener('fetch', () => {});

// -------- Notification click: focus app or open it --------
self.addEventListener('notificationclick', (e) => {
  e.notification.close();
  e.waitUntil((async () => {
    const windows = await self.clients.matchAll({ type: 'window', includeUncontrolled: true });
    // Prefer an already-open client inside our scope
    for (const c of windows) {
      if (c.url.startsWith(SCOPE) && 'focus' in c) return c.focus();
    }
    // Fallback: any open client
    for (const c of windows) { if ('focus' in c) return c.focus(); }
    if (self.clients.openWindow) return self.clients.openWindow(SCOPE);
  })());
});

// -------- Periodic Background Sync --------
// Chrome (desktop + Android) for installed PWAs only; others no-op.
// Browser enforces its own minInterval (typically ≥12h). We still
// register the tag so if the spec later expands, hydration nudges
// arrive even while the tab is closed.
self.addEventListener('periodicsync', (e) => {
  if (e.tag === 'water-reminder') {
    e.waitUntil(fireWaterReminderFromSW());
  } else if (e.tag === 'injection-reminder') {
    e.waitUntil(fireInjectionReminderFromSW());
  }
});

async function fireWaterReminderFromSW() {
  const now = new Date();
  const h = now.getHours();
  // Conservative waking window — page is the source of truth when open,
  // but the SW can't read localStorage, so we use a safe default here.
  if (h < 6 || h > 21) return;
  try {
    await self.registration.showNotification('💧 Time to hydrate', {
      body: 'Tap to log your water in the Health Tracker.',
      icon: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 96 96"><rect width="96" height="96" rx="18" fill="%230ea5e9"/><text x="50%" y="62%" text-anchor="middle" font-size="54" fill="white" font-family="Arial">💧</text></svg>',
      badge: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"><text x="50%" y="65%" text-anchor="middle" font-size="36" fill="black">💧</text></svg>',
      tag: 'tp-water-bg',
      renotify: false,
      requireInteraction: false,
    });
  } catch { /* noop */ }
}

async function fireInjectionReminderFromSW() {
  const now = new Date();
  const day = now.getDay(); // 0 = Sunday, 4 = Thursday
  if (day !== 0 && day !== 4) return;
  try {
    await self.registration.showNotification('💉 Injection Day', {
      body: 'Retatrutide + supporting compounds due today. Tap to log.',
      icon: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 96 96"><rect width="96" height="96" rx="18" fill="%2300d4ff"/><text x="50%" y="62%" text-anchor="middle" font-size="54" fill="white" font-family="Arial">💉</text></svg>',
      tag: 'tp-injection-bg',
      requireInteraction: false,
    });
  } catch { /* noop */ }
}

// -------- Message bridge: let the page fire SW-routed notifications --------
// The page calls registration.showNotification() directly in most cases.
// This message path exists for future use (e.g. scheduled reminders).
self.addEventListener('message', (e) => {
  const d = e.data || {};
  if (d.type === 'show-notification') {
    self.registration.showNotification(d.title || 'Health Tracker', d.options || {});
  }
});
