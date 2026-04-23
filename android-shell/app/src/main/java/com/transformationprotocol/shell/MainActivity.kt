package com.transformationprotocol.shell

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var pendingSyncRequest: PendingSyncRequest? = null

    private val requiredPermissions = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
    )

    private val permissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract(),
    ) { granted ->
        val pending = pendingSyncRequest ?: return@registerForActivityResult
        pendingSyncRequest = null
        lifecycleScope.launch {
            if (granted.containsAll(requiredPermissions)) {
                performHealthConnectSync(pending.requestId, pending.days)
            } else {
                dispatchPayload(
                    buildStatusPayload(
                        type = "sync-result",
                        requestId = pending.requestId,
                    ).apply {
                        put("ok", false)
                        put("message", "Health Connect permission was not granted.")
                    },
                )
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                dispatchPayload(buildStatusPayload())
            }
        }

        webView.addJavascriptInterface(
            HealthConnectBridge(this),
            "AndroidHealthConnect",
        )
        webView.loadUrl(BuildConfig.WEB_APP_URL)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    fun getHealthConnectStatusJson(): String = buildStatusPayload().toString()

    fun startHealthConnectSync(requestId: String, days: Int, interactive: Boolean) {
        runOnUiThread {
            lifecycleScope.launch {
                val availability = HealthConnectClient.getSdkStatus(this@MainActivity)
                when (availability) {
                    HealthConnectClient.SDK_UNAVAILABLE -> {
                        dispatchPayload(
                            buildStatusPayload(
                                type = "sync-result",
                                requestId = requestId,
                            ).apply {
                                put("ok", false)
                                put("message", "Health Connect is unavailable on this device.")
                            },
                        )
                    }

                    HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                        if (interactive) openHealthConnectOnboarding()
                        dispatchPayload(
                            buildStatusPayload(
                                type = "sync-result",
                                requestId = requestId,
                            ).apply {
                                put("ok", false)
                                put(
                                    "message",
                                    "Install or update Health Connect, then try again.",
                                )
                            },
                        )
                    }

                    else -> {
                        val client = HealthConnectClient.getOrCreate(this@MainActivity)
                        val granted =
                            client.permissionController.getGrantedPermissions()
                        if (!granted.containsAll(requiredPermissions)) {
                            if (!interactive) {
                                dispatchPayload(
                                    buildStatusPayload(
                                        type = "sync-result",
                                        requestId = requestId,
                                    ).apply {
                                        put("ok", false)
                                        put(
                                            "message",
                                            "Health Connect permission is still required.",
                                        )
                                    },
                                )
                                return@launch
                            }
                            pendingSyncRequest = PendingSyncRequest(
                                requestId = requestId,
                                days = days.coerceIn(1, 30),
                            )
                            permissionLauncher.launch(requiredPermissions)
                            return@launch
                        }
                        performHealthConnectSync(requestId, days.coerceIn(1, 30))
                    }
                }
            }
        }
    }

    fun openHealthConnectManageData() {
        lifecycleScope.launch {
            try {
                startActivity(HealthConnectClient.getHealthConnectManageDataIntent(this@MainActivity))
            } catch (_: Exception) {
                openHealthConnectOnboarding()
            }
        }
    }

    private fun openHealthConnectOnboarding() {
        val providerPackage = "com.google.android.apps.healthdata"
        val uriString =
            "market://details?id=$providerPackage&url=healthconnect%3A%2F%2Fonboarding"
        runCatching {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)
                },
            )
        }.getOrElse {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://play.google.com/store/apps/details?id=$providerPackage",
                    ),
                ),
            )
        }
    }

    private suspend fun performHealthConnectSync(requestId: String, days: Int) {
        val client = HealthConnectClient.getOrCreate(this)
        val zone = ZoneId.systemDefault()
        val endInstant = Instant.now()
        val startDate = LocalDate.now(zone).minusDays((days - 1).toLong())
        val startInstant = startDate.atStartOfDay(zone).toInstant()
        val dayMap = linkedMapOf<LocalDate, DailySnapshot>()
        var cursor = startDate
        repeat(days) {
            dayMap[cursor] = DailySnapshot(date = cursor)
            cursor = cursor.plusDays(1)
        }

        val timeRange = TimeRangeFilter.between(startInstant, endInstant)

        val weights = client.readRecords(
            ReadRecordsRequest<WeightRecord>(
                timeRangeFilter = timeRange,
            ),
        ).records
        weights.forEach { record ->
            val date = record.time.atZone(zone).toLocalDate()
            val snapshot = dayMap[date] ?: return@forEach
            val pounds = record.weight.inPounds
            if (
                snapshot.weightCapturedAt == null ||
                record.time.isAfter(snapshot.weightCapturedAt)
            ) {
                snapshot.weightLb = pounds
                snapshot.weightCapturedAt = record.time
            }
        }

        val heartRates = client.readRecords(
            ReadRecordsRequest<RestingHeartRateRecord>(
                timeRangeFilter = timeRange,
            ),
        ).records
        heartRates.forEach { record ->
            val date = record.time.atZone(zone).toLocalDate()
            val snapshot = dayMap[date] ?: return@forEach
            if (
                snapshot.restingHrCapturedAt == null ||
                record.time.isAfter(snapshot.restingHrCapturedAt)
            ) {
                snapshot.restingHr = record.beatsPerMinute.toDouble()
                snapshot.restingHrCapturedAt = record.time
            }
        }

        val sleeps = client.readRecords(
            ReadRecordsRequest<SleepSessionRecord>(
                timeRangeFilter = timeRange,
            ),
        ).records
        sleeps.forEach { record ->
            val date = record.endTime.atZone(zone).toLocalDate()
            val snapshot = dayMap[date] ?: return@forEach
            val hours = Duration.between(record.startTime, record.endTime).toMinutes() / 60.0
            if (hours > (snapshot.sleepHours ?: 0.0)) {
                snapshot.sleepHours = hours
            }
        }

        val steps = client.readRecords(
            ReadRecordsRequest<StepsRecord>(
                timeRangeFilter = timeRange,
            ),
        ).records
        steps.forEach { record ->
            val date = record.endTime.atZone(zone).toLocalDate()
            val snapshot = dayMap[date] ?: return@forEach
            snapshot.steps = (snapshot.steps ?: 0.0) + record.count.toDouble()
        }

        val daysJson = JSONArray()
        dayMap.values.forEach { day ->
            val json = JSONObject().put("date", day.date.toString())
            if (day.weightLb != null) json.put("weightLb", round1(day.weightLb))
            if (day.steps != null) json.put("steps", day.steps!!.toLong())
            if (day.sleepHours != null) json.put("sleepHours", round1(day.sleepHours))
            if (day.restingHr != null) json.put("restingHr", day.restingHr!!.toLong())
            daysJson.put(json)
        }

        val latest = dayMap.values
            .toList()
            .asReversed()
            .firstOrNull {
                it.weightLb != null ||
                    it.steps != null ||
                    it.sleepHours != null ||
                    it.restingHr != null
            }

        dispatchPayload(
            buildStatusPayload(
                type = "sync-result",
                requestId = requestId,
            ).apply {
                put("ok", true)
                put("lastSync", Instant.now().toString())
                put("days", daysJson)
                put(
                    "summary",
                    JSONObject().apply {
                        if (latest?.weightLb != null) put("weightLb", round1(latest.weightLb))
                        if (latest?.steps != null) put("steps", latest.steps!!.toLong())
                        if (latest?.sleepHours != null) put("sleepHours", round1(latest.sleepHours))
                        if (latest?.restingHr != null) put("restingHr", latest.restingHr!!.toLong())
                    },
                )
                put("lastSyncSummary", buildSummary(latest))
            },
        )
    }

    private fun buildStatusPayload(
        type: String = "bridge-status",
        requestId: String? = null,
    ): JSONObject {
        val availability = HealthConnectClient.getSdkStatus(this)
        val grantedNow = runCatching {
            if (availability == HealthConnectClient.SDK_AVAILABLE) {
                // Review note: the web app asks for immediate bridge status, so
                // the shell reads granted permissions synchronously here instead
                // of caching stale values across permission changes.
                runBlocking {
                    HealthConnectClient
                        .getOrCreate(this@MainActivity)
                        .permissionController
                        .getGrantedPermissions()
                }
            } else {
                emptySet()
            }
        }.getOrDefault(emptySet())

        return JSONObject().apply {
            put("type", type)
            if (requestId != null) put("requestId", requestId)
            put("available", availability == HealthConnectClient.SDK_AVAILABLE)
            put("sdkStatus", sdkStatusLabel(availability))
            put("permissionGranted", grantedNow.containsAll(requiredPermissions))
            put("bridgeVersion", "1")
            put("shellVersion", BuildConfig.VERSION_NAME)
            put("webAppUrl", BuildConfig.WEB_APP_URL)
        }
    }

    private fun dispatchPayload(payload: JSONObject) {
        if (!::webView.isInitialized) return
        val js = "window.__healthConnectBridgeReceive(${JSONObject.quote(payload.toString())});"
        webView.post {
            webView.evaluateJavascript(js, null)
        }
    }

    private fun sdkStatusLabel(status: Int): String = when (status) {
        HealthConnectClient.SDK_AVAILABLE -> "available"
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
            "provider_update_required"
        else -> "unavailable"
    }

    private fun buildSummary(day: DailySnapshot?): String {
        if (day == null) return ""
        val parts = mutableListOf<String>()
        day.weightLb?.let { parts += "${round1(it)} lb" }
        day.steps?.let { parts += "${it.toLong()} steps" }
        day.sleepHours?.let { parts += "${round1(it)}h sleep" }
        day.restingHr?.let { parts += "${it.toLong()} bpm" }
        return parts.joinToString(" | ")
    }

    private fun round1(value: Double?): Double {
        val input = value ?: 0.0
        return kotlin.math.round(input * 10.0) / 10.0
    }

    private data class PendingSyncRequest(
        val requestId: String,
        val days: Int,
    )

    private data class DailySnapshot(
        val date: LocalDate,
        var weightLb: Double? = null,
        var steps: Double? = null,
        var sleepHours: Double? = null,
        var restingHr: Double? = null,
        var weightCapturedAt: Instant? = null,
        var restingHrCapturedAt: Instant? = null,
    )
}
