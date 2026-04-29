package com.transformationprotocol.shell

import android.webkit.JavascriptInterface

class HealthConnectBridge(
    private val activity: MainActivity,
) {
    @JavascriptInterface
    fun getStatusJson(): String = activity.getHealthConnectStatusJson()

    @JavascriptInterface
    fun getHealthConnectStatusJson(): String = activity.getHealthConnectStatusJson()

    @JavascriptInterface
    fun sync(): String = activity.requestHealthConnectSyncJson(days = 7, interactive = true)

    @JavascriptInterface
    fun requestHealthConnectSync(): String =
        activity.requestHealthConnectSyncJson(days = 7, interactive = true)

    @JavascriptInterface
    fun requestHealthConnectSyncDays(days: Int): String =
        activity.requestHealthConnectSyncJson(days = days, interactive = true)

    @JavascriptInterface
    fun openManageData(): String {
        activity.openHealthConnectManageData()
        return """{"type":"manage-data-request","ok":true}"""
    }

    @JavascriptInterface
    fun openHealthConnectManageData(): String {
        activity.openHealthConnectManageData()
        return """{"type":"manage-data-request","ok":true}"""
    }
}
