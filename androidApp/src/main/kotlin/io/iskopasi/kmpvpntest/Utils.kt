package io.iskopasi.kmpvpntest

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable,
    val isSystemApp: Boolean,
    val isChecked: Boolean,
)

suspend fun getAllApps(application: Application, showSystemApps: Boolean) =
    mutableListOf<AppInfo>().apply {
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
                AppInfo(
                    name = appInfo.loadLabel(pm).toString(),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(pm),
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    isChecked = false
                )
            }
            .sortedBy { it.name.lowercase() }
    }