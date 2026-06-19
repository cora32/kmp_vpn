package io.iskopasi.splittunnel.managers

import android.app.Application
import io.iskopasi.splittunnel.getAllApps
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppManagerImpl : AppManager, KoinComponent {
    private val application: Application by inject()

    override suspend fun getApps(
        selectedAppsMap: Map<String, Boolean>,
        showSystemApps: Boolean
    ): List<AppManagerData> = getAllApps(
        application,
        selectedAppsMap,
        showSystemApps
    )
}

actual fun getAppManager(): AppManager = AppManagerImpl()