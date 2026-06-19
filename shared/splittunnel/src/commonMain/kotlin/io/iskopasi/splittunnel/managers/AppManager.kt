package io.iskopasi.splittunnel.managers

import io.iskopasi.splittunnel.IconType

data class AppManagerData(
    val name: String,
    val packageName: String,
    val icon: IconType,
    val isSystemApp: Boolean,
    var isChecked: Boolean
)

interface AppManager {
    suspend fun getApps(
        selectedAppsMap: Map<String, Boolean>,
        showSystemApps: Boolean
    ): List<AppManagerData>
}

expect fun getAppManager(): AppManager