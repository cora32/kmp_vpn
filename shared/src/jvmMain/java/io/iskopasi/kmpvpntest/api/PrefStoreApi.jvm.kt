package io.iskopasi.kmpvpntest.api

import java.util.prefs.Preferences

class DesktopPrefStoreApi : PrefStoreApi {
    private val prefs = Preferences.userNodeForPackage(PrefStoreApi::class.java)

    override var allowedApps: Set<String>
        get() = prefs.get("allowed_apps", "").split(",").filter { it.isNotEmpty() }.toSet()
        set(value) {
            prefs.put("allowed_apps", value.joinToString(","))
        }

    override var allowAllApps: Boolean
        get() = prefs.getBoolean("allow_all_apps", true)
        set(value) {
            prefs.putBoolean("allow_all_apps", value)
        }
}

actual fun getPrefStore(): PrefStoreApi = DesktopPrefStoreApi()
