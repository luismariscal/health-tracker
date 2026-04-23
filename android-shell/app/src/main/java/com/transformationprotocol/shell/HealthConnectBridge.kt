package com.transformationprotocol.shell

import android.webkit.JavascriptInterface

class HealthConnectBridge(
    private val activity: MainActivity,
) {
    @JavascriptInterface
    fun getStatusJson(): String = activity.getHealthConnectStatusJson()

    @JavascriptInterface
    fun syncRecent(requestId: String, days: Int, interactive: Boolean) {
        activity.startHealthConnectSync(requestId, days, interactive)
    }

    @JavascriptInterface
    fun openManageData() {
        activity.openHealthConnectManageData()
    }
}
