package io.iskopasi.splittunnel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import io.iskopasi.splittunnel.managers.AppManagerData
import kotlinx.coroutines.coroutineScope


suspend fun getAllApps(
    application: Application,
    selectedAppsMap: Map<String, Boolean>,
    showSystemApps: Boolean
): List<AppManagerData> = coroutineScope {
    val thisPackage = application.packageName
    val pm = application.packageManager

    pm.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter { appInfo ->
            val curApp = appInfo.packageName == thisPackage
            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystemApp =
                (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            (if (showSystemApps)
                true
            else
                (!isSystemApp || isUpdatedSystemApp)) && !curApp
        }
        .map { appInfo ->
            AppManagerData(
                name = appInfo.loadLabel(pm).toString(),
                packageName = appInfo.packageName,
                icon = appInfo,
                isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                isChecked = selectedAppsMap.getOrDefault(appInfo.packageName, false)
            )
        }
        .sortedBy { it.name.lowercase() }
}