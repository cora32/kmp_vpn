package io.iskopasi.kmpvpntest.api

import io.iskopasi.kmpvpntest.managers.ProxyData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DesktopPrefStoreApi : PrefStoreApi {
    private val prefs = FilePrefs()

    override var proxyData: ProxyData
        get() {
            val json = prefs.get("proxy_data", "")
            return if (json.isEmpty()) {
                ProxyData.empty
            } else {
                try {
                    Json.decodeFromString<ProxyData>(json)
                } catch (e: Exception) {
                    ProxyData.empty
                }
            }
        }
        set(value) {
            prefs.put("proxy_data", Json.encodeToString(value))
        }

    override var allowedApps: Set<String>
        get() = prefs.get("allowed_apps", "").split(",").filter { it.isNotEmpty() }.toSet()
        set(value) {
            prefs.put("allowed_apps", value.joinToString(","))

            val namesOnly = value.map { it.substringAfterLast("\\") }
            allowedAppsNamesOnly = namesOnly.toSet()
        }

    override var allowedAppsNamesOnly: Set<String>
        get() = prefs.get("allowed_apps_names_names", "").split(",").filter { it.isNotEmpty() }
            .toSet()
        set(value) {
            prefs.put("allowed_apps_names_names", value.joinToString(","))
        }

    override var routeAllApps: Boolean
        get() = prefs.getBoolean("allow_all_apps", true)
        set(value) {
            prefs.putBoolean("allow_all_apps", value)
        }
    override var showSystemApps: Boolean = true
}

actual fun getPrefStore(): PrefStoreApi = DesktopPrefStoreApi()
