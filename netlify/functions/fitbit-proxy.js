"use strict";

// Review note: this bridge only proxies Fitbit read endpoints. The browser app
// still owns OAuth PKCE and stores the user's token locally; the bridge exists
// to bypass browser-level endpoint/network issues on Activity/Sleep reads.
exports.handler = async function fitbitProxy(event) {
  const allowOrigin = process.env.FITBIT_BRIDGE_ALLOW_ORIGIN || "*";
  const corsHeaders = {
    "access-control-allow-origin": allowOrigin,
    "access-control-allow-methods": "POST,OPTIONS",
    "access-control-allow-headers": "content-type",
    "content-type": "application/json; charset=utf-8",
  };

  if (event.httpMethod === "OPTIONS") {
    return {
      statusCode: 204,
      headers: corsHeaders,
      body: "",
    };
  }

  if (event.httpMethod !== "POST") {
    return {
      statusCode: 405,
      headers: corsHeaders,
      body: JSON.stringify({ ok: false, error: "Use POST" }),
    };
  }

  try {
    const payload = JSON.parse(event.body || "{}");
    const path = String(payload.path || "");
    const accessToken = String(payload.accessToken || "");

    if (!path.startsWith("/")) {
      return {
        statusCode: 400,
        headers: corsHeaders,
        body: JSON.stringify({ ok: false, error: "Path must start with /" }),
      };
    }
    if (!accessToken) {
      return {
        statusCode: 400,
        headers: corsHeaders,
        body: JSON.stringify({ ok: false, error: "Missing access token" }),
      };
    }

    const resp = await fetch("https://api.fitbit.com" + path, {
      headers: {
        authorization: "Bearer " + accessToken,
        "accept-language": "en_US",
      },
    });
    const bodyText = await resp.text();
    let body = null;
    try {
      body = bodyText ? JSON.parse(bodyText) : null;
    } catch {
      body = null;
    }

    return {
      statusCode: 200,
      headers: corsHeaders,
      body: JSON.stringify({
        ok: resp.ok,
        status: resp.status,
        body,
        bodyText,
      }),
    };
  } catch (error) {
    return {
      statusCode: 500,
      headers: corsHeaders,
      body: JSON.stringify({
        ok: false,
        error: error && error.message ? error.message : "Bridge failed",
      }),
    };
  }
};
