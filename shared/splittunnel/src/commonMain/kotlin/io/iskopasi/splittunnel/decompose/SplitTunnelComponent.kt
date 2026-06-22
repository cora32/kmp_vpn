package io.iskopasi.splittunnel.decompose

import com.arkivanov.decompose.value.Value
import io.iskopasi.splittunnel.IconType
import io.iskopasi.splittunnel.managers.AppManagerData
import kotlinx.coroutines.flow.StateFlow

data class AndroidAppData(
    val name: String,
    val packageName: String,
    val icon: IconType,
    val isSystemApp: Boolean,
    val isChecked: Boolean
)

interface SplitTunnelComponent {
    val model: Value<Model>
    val isLoading: StateFlow<Boolean>
    val appList: StateFlow<List<AppManagerData>>
    val showSystemAppsFlow: StateFlow<Boolean>
    val routeAllAppsFlow: StateFlow<Boolean>

    data class Model(
        val selectedApps: List<String>,
        val runningProcesses: List<String>,
        // Android data
        val appList: List<AndroidAppData> = emptyList()
    )

    fun onAddApp(path: String)
    fun onRemoveApp(path: String)
    fun onSelectFile()
    fun onRefreshProcesses()


    // Android methods
    fun toggleShowSystemApps(value: Boolean)

    fun toggleRouteAllApps(value: Boolean)

    fun onCheckApp(packageName: String, value: Boolean)
}
