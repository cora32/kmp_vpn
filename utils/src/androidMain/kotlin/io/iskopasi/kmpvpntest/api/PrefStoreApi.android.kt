package io.iskopasi.kmpvpntest.api

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import io.iskopasi.kmpvpntest.managers.ProxyData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.inject

class AndroidPrefStoreApi() : PrefStoreApi {
    private val application: Application by inject()

    private val sp by lazy {
        application.getSharedPreferences("kmp_vpn", Context.MODE_PRIVATE)
    }

    override var proxyData: ProxyData
        get() {
            sp.getString("proxy_data", "")?.let {
                if (it.isEmpty()) return ProxyData.empty

                return Json.decodeFromString<ProxyData>(it)
            }

            return ProxyData.empty
        }
        set(value) {
            sp.edit(commit = true) {
                putString("proxy_data", Json.encodeToString(value))
            }
        }

    override var allowedApps: Set<String>
        get() = sp.getStringSet("allowed_apps", emptySet()) ?: emptySet()
        set(value) {
            sp.edit(commit = true) {
                putStringSet("allowed_apps", value)
            }

            val namesOnly = value.map { it.substringAfterLast("\\") }
            allowedAppsNamesOnly = namesOnly.toSet()
        }

    override var allowedAppsNamesOnly: Set<String>
        get() = sp.getStringSet("allowed_apps_names_only", emptySet()) ?: emptySet()
        set(value) {
            sp.edit(commit = true) {
                putStringSet("allowed_apps_names_only", value)
            }
        }

    override var filterList: String
        get() = sp.getString("filter_list", "") ?: ""
        set(value) {
            sp.edit(commit = true) {
                putString("filter_list", value)
            }
        }

    override var routeAllApps: Boolean
        get() = sp.getBoolean("allow_all_apps", true)
        set(value) {
            sp.edit(commit = true) {
                putBoolean("allow_all_apps", value)
            }
        }

    override var showSystemApps: Boolean
        get() = sp.getBoolean("show_system_apps", true)
        set(value) {
            sp.edit(commit = true) {
                putBoolean("show_system_apps", value)
            }
        }

    override var isAuthEnabled: Boolean
        get() = sp.getBoolean("is_auth_enabled", false)
        set(value) {
            sp.edit(commit = true) {
                putBoolean("is_auth_enabled", value)
            }
        }

    override var isCertCheckEnabled: Boolean
        get() = sp.getBoolean("is_cert_check_enabled", true)
        set(value) {
            sp.edit(commit = true) {
                putBoolean("is_cert_check_enabled", value)
            }
        }
}

actual fun getPrefStore(): PrefStoreApi = AndroidPrefStoreApi()
