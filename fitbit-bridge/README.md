# Fitbit Bridge

This folder documents the optional Fitbit serverless bridge used by the browser
app when direct Fitbit API calls fail for Activity or Sleep.

## Why this exists

The tracker uses browser-based Fitbit OAuth PKCE, which is fine for login and
some read calls. In practice, some users see:

- weight succeeds
- activity fails with `Failed to fetch`
- sleep fails with `Failed to fetch`

This bridge moves Fitbit data reads to a server-to-server call so the browser
is no longer the one talking directly to the failing Fitbit endpoints.

## Netlify deploy

1. Create a Netlify site connected to this repo.
2. Deploy the `netlify/functions/fitbit-proxy.js` function.
3. Optional but recommended: set `FITBIT_BRIDGE_ALLOW_ORIGIN` to your app URL.
   Example:
   - `https://luismariscal.github.io`
4. After deploy, copy the function URL. It usually looks like:
   - `https://your-site.netlify.app/.netlify/functions/fitbit-proxy`
5. In the app:
   - open Fitbit settings
   - paste the function URL into `Fitbit Bridge URL`
   - click `Save Bridge`

## Security note

This bridge intentionally does **not** store Fitbit user tokens. The browser
still holds the access token locally and sends it to the bridge per request.
That keeps this first pass small, but it also means the bridge is a transport
proxy, not a full secure token backend.

If we later want a stronger production model, the next step would be moving the
Fitbit token exchange and refresh flow server-side too.
