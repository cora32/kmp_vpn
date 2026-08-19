package io.iskopasi.splittunnel.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.splittunnel.managers.AppManager
import io.iskopasi.splittunnel.managers.AppManagerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ISplitTunnelViewModel {
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

    fun getAppList()
}

abstract class SplitTunnelBaseViewModel : ISplitTunnelViewModel, ViewModel(), KoinComponent {
    protected val prefStore: PrefStoreApi by inject()
    protected val appManager: AppManager by inject()

    private var currentAppList = listOf<AppManagerData>()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isLoading = _isLoading.asStateFlow()

    protected val _routeAllAppsFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(prefStore.routeAllApps)
    override val routeAllAppsFlow = _routeAllAppsFlow.asStateFlow()
    protected val _showSystemAppsFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(prefStore.showSystemApps)

    override val showSystemAppsFlow = _showSystemAppsFlow.asStateFlow()
    protected val _appList: MutableStateFlow<List<AppManagerData>> = MutableStateFlow(emptyList())
    override val appList = _appList.asStateFlow()
    protected val _allowedAppsFlow: MutableStateFlow<List<AppManagerData>> =
        MutableStateFlow(emptyList())
    override val allowedAppsFlow = _allowedAppsFlow.asStateFlow()
    protected val _runningProcessesFlow = MutableStateFlow(emptyList<AppManagerData>())
    override val runningProcessesFlow = _runningProcessesFlow

    override fun getAppList() {
        _isLoading.update {
            true
        }

        viewModelScope.launch(Dispatchers.IO) {
            val allApps = appManager.getApps(
                selectedAppsMap = emptyMap(),
                showSystemApps = _showSystemAppsFlow.value
            )

            currentAppList = allApps

            resortAppList(showSystemApp = _showSystemAppsFlow.value)

            _isLoading.update {
                false
            }
        }
    }

    override fun onSelectFile() {}

    override fun getProcessList() {

    }

    override fun onAddApp(data: AppManagerData) {

    }

    override fun onRemoveApp(data: AppManagerData) {

    }

    protected suspend fun resortAppList(showSystemApp: Boolean) = coroutineScope {
        val allowedApps = prefStore.allowedApps

        val appsWithSystem = if (showSystemApp) currentAppList.map {
            it.copy(isChecked = allowedApps.contains(it.packageName))
        }
        else
            currentAppList.map {
                it.copy(isChecked = allowedApps.contains(it.packageName))
            }
                .filter { !it.isSystemApp }
        val sorted = appsWithSystem
            .sortedWith(compareByDescending<AppManagerData> { it.isChecked }.thenBy { it.name.lowercase() })

        _appList.update {
            sorted
        }
    }

    override fun onCheckApp(packageName: String, value: Boolean) {

    }

    protected fun onAppListChanged(apps: Set<String>) {
        if (apps.isEmpty()) {
            prefStore.routeAllApps = true
            prefStore.allowedApps = emptySet()
        } else {
            prefStore.routeAllApps = false
            prefStore.allowedApps = apps
        }
    }

    override fun toggleRouteAllApps(value: Boolean) {
        _routeAllAppsFlow.update {
            value
        }

        prefStore.routeAllApps = value
    }

    override fun toggleShowSystemApps(value: Boolean) {
        _showSystemAppsFlow.update {
            value
        }

        prefStore.showSystemApps = value

        viewModelScope.launch(Dispatchers.IO) {
            resortAppList(showSystemApp = value)
        }
    }
}

expect fun getSplitTunnelViewModel(): ISplitTunnelViewModel