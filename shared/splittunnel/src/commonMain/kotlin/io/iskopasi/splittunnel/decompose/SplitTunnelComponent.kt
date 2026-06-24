package io.iskopasi.splittunnel.decompose

import io.iskopasi.splittunnel.managers.AppManagerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SplitTunnelComponent {
    val isLoading: StateFlow<Boolean>
    val appList: StateFlow<List<AppManagerData>>
    val showSystemAppsFlow: StateFlow<Boolean>
    val routeAllAppsFlow: StateFlow<Boolean>

    val allowedAppsFlow: StateFlow<List<AppManagerData>>
    val runningProcessesFlow: StateFlow<List<AppManagerData>>

    fun onAddApp(data: AppManagerData)
    fun onRemoveApp(data: AppManagerData)
    fun onSelectFile()
    fun getProcessList()


    // Android methods
    fun toggleShowSystemApps(value: Boolean)

    fun toggleRouteAllApps(value: Boolean)

    fun onCheckApp(packageName: String, value: Boolean)
}

abstract class SplitTunnelComponentAbstract : SplitTunnelComponent {
    override val appList = MutableStateFlow(emptyList<AppManagerData>()).asStateFlow()
    override val showSystemAppsFlow = MutableStateFlow(false).asStateFlow()
    override val routeAllAppsFlow = MutableStateFlow(true).asStateFlow()
    override val allowedAppsFlow = MutableStateFlow(emptyList<AppManagerData>()).asStateFlow()
    override val runningProcessesFlow = MutableStateFlow(emptyList<AppManagerData>()).asStateFlow()
}