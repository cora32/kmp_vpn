//package io.iskopasi.splittunnel.decompose
//
//import com.arkivanov.decompose.ComponentContext
//import io.iskopasi.splittunnel.getRunningProcesses
//import io.iskopasi.splittunnel.managers.AppManagerData
//import io.iskopasi.splittunnel.pickExeFile
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import org.koin.core.component.KoinComponent
//
//class SplitTunnelComponentImpl(
//    componentContext: ComponentContext
//) : SplitTunnelComponentAbstract(), ComponentContext by componentContext, KoinComponent {
////    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//
//    private val _routeAllAppsFlow: MutableStateFlow<Boolean> =
//        MutableStateFlow(prefStore.routeAllApps)
//    private val _showSystemAppsFlow: MutableStateFlow<Boolean> =
//        MutableStateFlow(prefStore.showSystemApps)
//    private val _appList: MutableStateFlow<List<AppManagerData>> = MutableStateFlow(emptyList())
//    private val allowedAppsMap = mutableMapOf<String, Boolean>()
//
//    override val routeAllAppsFlow = _routeAllAppsFlow.asStateFlow()
//    override val showSystemAppsFlow = _showSystemAppsFlow.asStateFlow()
//    override val appList = _appList.asStateFlow()
//
//    private var currentAppList = listOf<AppManagerData>()
//
//    init {
//        getAppList()
//
//        scope.launch {
//            _allowedAppsFlow.update {
//                prefStore.allowedApps.map {
//                    AppManagerData("", it, icon = null, isSystemApp = false, isChecked = true)
//                }
//            }
//        }
//    }
//
////    private fun onAppListChanged(apps: Set<String>) {
////        if (apps.isEmpty()) {
////            prefStore.routeAllApps = true
////            prefStore.allowedApps = emptySet()
////        } else {
////            prefStore.routeAllApps = false
////            prefStore.allowedApps = apps
////        }
////    }
//
//    // ==================== Desktop ====================
//
//    override fun getProcessList() {
//        scope.launch {
//            val processList = getRunningProcesses()
//
//            _runningProcessesFlow.update {
//                processList
//            }
//        }
//    }
//
//    override fun onAddApp(data: AppManagerData) {
//        scope.launch {
//            data.isChecked = true
//            prefStore.allowedApps += data.packageName
//
//            resortAppList(showSystemApp = true)
//
//            refreshAllowedFlow()
//        }
//    }
//
//    override fun onRemoveApp(data: AppManagerData) {
//        scope.launch {
//            data.isChecked = false
//            prefStore.allowedApps -= data.packageName
//
//            resortAppList(showSystemApp = true)
//
//            refreshAllowedFlow()
//        }
//    }
//
//    private fun refreshAllowedFlow() {
//        scope.launch {
//            _allowedAppsFlow.update {
//                prefStore.allowedApps.map {
//                    AppManagerData("", it, icon = null, isSystemApp = false, isChecked = true)
//                }
//            }
//        }
//    }
//
//    override fun onSelectFile() {
//        pickExeFile()?.let {
//            onAddApp(AppManagerData("", it, icon = null, isSystemApp = false, isChecked = true))
//        }
//    }
//
//    // ==================== /Desktop ====================
//
//    // ==================== Android ====================
////
////    override fun toggleRouteAllApps(value: Boolean) {
////        _routeAllAppsFlow.update {
////            value
////        }
////
////        prefStore.routeAllApps = value
////    }
////
////    override fun toggleShowSystemApps(value: Boolean) {
////        _showSystemAppsFlow.update {
////            value
////        }
////
////        prefStore.showSystemApps = value
////
////        scope.launch {
////            resortAppList(showSystemApp = value)
////        }
////    }
//
//    override fun onCheckApp(packageName: String, value: Boolean) {
//        allowedAppsMap[packageName] = value
//        onAppListChanged(allowedAppsMap.filterValues { it }.keys)
//
//        scope.launch {
//            resortAppList(showSystemApp = _showSystemAppsFlow.value)
//        }
//    }
//
//    private suspend fun resortAppList(showSystemApp: Boolean) = coroutineScope {
//        val appsWithSystem = if (showSystemApp) currentAppList.map {
//            it.copy(isChecked = allowedAppsMap[it.packageName] ?: false)
//        }
//        else
//            currentAppList.map {
//                it.copy(isChecked = allowedAppsMap[it.packageName] ?: false)
//            }
//                .filter { !it.isSystemApp }
//        val sorted = appsWithSystem
//            .sortedWith(compareByDescending<AppManagerData> { it.isChecked }.thenBy { it.name.lowercase() })
//
//        _appList.update {
//            sorted
//        }
//    }
//
//    private fun getAppList() {
//        _isLoading.update {
//            true
//        }
//
//        scope.launch {
//            val allApps = appManager.getApps(
//                selectedAppsMap = allowedAppsMap,
//                showSystemApps = _showSystemAppsFlow.value
//            )
//
//            currentAppList = allApps
//
//            resortAppList(showSystemApp = _showSystemAppsFlow.value)
//
//            _isLoading.update {
//                false
//            }
//        }
//    }
//
//    // ==================== /Android ====================
//}
