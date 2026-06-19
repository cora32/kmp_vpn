package io.iskopasi.splittunnel.managers

// Not used on Desktop
actual fun getAppManager(): AppManager = object : AppManager {
    override suspend fun getApps(
        selectedAppsMap: Map<String, Boolean>,
        showSystemApps: Boolean
    ): List<AppManagerData> {
        return emptyList()
    }
}