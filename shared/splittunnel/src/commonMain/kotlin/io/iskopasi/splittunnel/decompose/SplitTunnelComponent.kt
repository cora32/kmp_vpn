package io.iskopasi.splittunnel.decompose

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.e
import io.iskopasi.splittunnel.managers.AppManager
import io.iskopasi.splittunnel.managers.AppManagerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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

    fun getAppList()
}

abstract class SplitTunnelComponentAbstract : SplitTunnelComponent, KoinComponent {
    protected val prefStore: PrefStoreApi by inject()
    protected val appManager: AppManager by inject()

    protected val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentAppList = listOf<AppManagerData>()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isLoading = _isLoading.asStateFlow()
//    protected val allowedAppsMap = mutableMapOf<String, Boolean>()

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

//    init {
//        restoreAllowedMap()
//    }
//
//    private fun restoreAllowedMap() {
//        prefStore.allowedApps.forEach {
//            allowedAppsMap[it] = true
//        }
//    }

    override fun getAppList() {
        _isLoading.update {
            true
        }

        scope.launch {
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
        "===> toggleShowSystemApps: $value".e
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

        scope.launch {
            resortAppList(showSystemApp = value)
        }
    }
}