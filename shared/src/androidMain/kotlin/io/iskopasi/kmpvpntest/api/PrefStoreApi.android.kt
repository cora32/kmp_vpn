package io.iskopasi.kmpvpntest.api

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPrefStoreApi : PrefStoreApi, KoinComponent {
    // We use Koin to inject the Application context
    private val application: Application by inject()

    private val sp by lazy {
        application.getSharedPreferences("kmp_vpn", Context.MODE_PRIVATE)
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
