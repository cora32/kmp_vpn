package io.iskopasi.kmpvpntest.api

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import io.iskopasi.kmpvpntest.managers.ProxyData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPrefStoreApi : PrefStoreApi, KoinComponent {
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
        }

    override var allowAllApps: Boolean
        get() = sp.getBoolean("allow_all_apps", true)
        set(value) {
            sp.edit(commit = true) {
                putBoolean("allow_all_apps", value)
            }
        }
}

actual fun getPrefStore(): PrefStoreApi = AndroidPrefStoreApi()
