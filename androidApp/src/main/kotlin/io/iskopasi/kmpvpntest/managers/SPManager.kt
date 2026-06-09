package io.iskopasi.kmpvpntest.managers

import android.app.Application
import android.content.Context
import androidx.core.content.edit

class SPManager(
    application: Application
) {
    private val sp =
        application.getSharedPreferences("kmp_vpn", Context.MODE_PRIVATE)

    var allowedApps: Set<String>
        get() = sp.getStringSet("allowed_apps", emptySet()) ?: emptySet()
        set(value) {
            sp.edit(commit = true) {
                putStringSet("allowed_apps", value)
            }
        }

    var allowAllApps: Boolean
        get() = sp.getBoolean("allow_all_apps", true)
        set(value) {
            sp.edit(commit = true) {
                putBoolean("allow_all_apps", value)
            }
        }
}