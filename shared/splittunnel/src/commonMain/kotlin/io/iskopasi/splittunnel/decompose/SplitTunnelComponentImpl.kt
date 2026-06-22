package io.iskopasi.splittunnel.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.splittunnel.getRunningProcesses
import io.iskopasi.splittunnel.managers.AppManager
import io.iskopasi.splittunnel.managers.AppManagerData
import io.iskopasi.splittunnel.pickExeFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplitTunnelComponentImpl(
    componentContext: ComponentContext,
    private val initialApps: Set<String>,
    private val onAppListChanged: (Set<String>) -> Unit
) : SplitTunnelComponent, ComponentContext by componentContext, KoinComponent {
    private val prefStore: PrefStoreApi by inject()
    private val appManager: AppManager by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    private val _routeAllAppsFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(prefStore.routeAllApps)
    private val _showSystemAppsFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(prefStore.showSystemApps)
    private val _appList: MutableStateFlow<List<AppManagerData>> = MutableStateFlow(emptyList())
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val selectedAppsMap = mutableMapOf<String, Boolean>()

    override val routeAllAppsFlow = _routeAllAppsFlow.asStateFlow()
    override val showSystemAppsFlow = _showSystemAppsFlow.asStateFlow()
    override val appList = _appList.asStateFlow()
    override val isLoading = _loading.asStateFlow()

    private var currentAppList = listOf<AppManagerData>()

    init {
        initialApps.forEach {
            selectedAppsMap[it] = true
        }
        getAppList()
    }

    // ==================== Desktop ====================
    private val _model = MutableValue(
        SplitTunnelComponent.Model(
            selectedApps = initialApps.toList(),
            runningProcesses = emptyList()
        )
    )
    override val model: Value<SplitTunnelComponent.Model> = _model

    override fun onAddApp(path: String) {
        val fileName = if (path.contains("\\") || path.contains("/")) {
            path.substringAfterLast("\\").substringAfterLast("/")
        } else {
            path
        }

        if (fileName.isNotEmpty() && !_model.value.selectedApps.contains(fileName)) {
            _model.update { it.copy(selectedApps = it.selectedApps + fileName) }
            onAppListChanged(_model.value.selectedApps.toSet())
        }
    }

    override fun onRemoveApp(path: String) {
        _model.update { it.copy(selectedApps = it.selectedApps - path) }
        onAppListChanged(_model.value.selectedApps.toSet())
    }

    override fun onSelectFile() {
        pickExeFile()?.let { onAddApp(it) }
    }

    override fun onRefreshProcesses() {
        val processes = getRunningProcesses()
        _model.update { it.copy(runningProcesses = processes) }
    }
    // ==================== /Desktop ====================

    // ==================== Android ====================

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

        resortAppList(showSystemApp = value)
    }

    override fun onCheckApp(packageName: String, value: Boolean) {
        selectedAppsMap[packageName] = value
        onAppListChanged(selectedAppsMap.filterValues { it }.keys)

        resortAppList(showSystemApp = _showSystemAppsFlow.value)
    }

    private fun resortAppList(showSystemApp: Boolean) {
        val sorted = currentAppList.map {
            it.copy(isChecked = selectedAppsMap[it.packageName] ?: false)
        }
            .filter { it.isSystemApp == showSystemApp }
            .sortedWith(compareByDescending<AppManagerData> { it.isChecked }.thenBy { it.name.lowercase() })

        _appList.update {
            sorted
        }
    }

    private fun getAppList() {
        _loading.update {
            true
        }

        scope.launch {
            val allApps = appManager.getApps(
                selectedAppsMap = selectedAppsMap,
                showSystemApps = _showSystemAppsFlow.value
            )

            currentAppList = allApps

            resortAppList(showSystemApp = _showSystemAppsFlow.value)

            _loading.update {
                false
            }
        }
    }

    // ==================== /Android ====================
}
