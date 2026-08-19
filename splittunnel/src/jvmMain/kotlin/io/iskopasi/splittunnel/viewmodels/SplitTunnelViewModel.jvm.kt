package io.iskopasi.splittunnel.viewmodels

import androidx.lifecycle.viewModelScope
import io.iskopasi.splittunnel.getRunningProcesses
import io.iskopasi.splittunnel.managers.AppManagerData
import io.iskopasi.splittunnel.pickExeFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.io.File

class SplitTunnelViewModelDesktop : SplitTunnelBaseViewModel(), KoinComponent {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            _allowedAppsFlow.update {
                prefStore.allowedApps.map {
                    AppManagerData(
                        name = File(it).nameWithoutExtension,
                        packageName = it,
                        icon = it,
                        isSystemApp = false,
                        isChecked = true
                    )
                }
            }
        }
    }

    override fun getProcessList() {
        viewModelScope.launch(Dispatchers.IO) {
            val processList = getRunningProcesses()

            _runningProcessesFlow.update {
                processList
            }
        }
    }

    override fun onAddApp(data: AppManagerData) {
        viewModelScope.launch(Dispatchers.IO) {
            data.isChecked = true
            prefStore.allowedApps += data.packageName

            resortAppList(showSystemApp = true)

            refreshAllowedFlow()
        }
    }

    override fun onRemoveApp(data: AppManagerData) {
        viewModelScope.launch(Dispatchers.IO) {
            data.isChecked = false
            prefStore.allowedApps -= data.packageName

            resortAppList(showSystemApp = true)

            refreshAllowedFlow()
        }
    }

    private fun refreshAllowedFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            val runningProcesses = _runningProcessesFlow.value
            _allowedAppsFlow.update {
                prefStore.allowedApps.map { path ->
                    val existing = runningProcesses.find { it.packageName == path }
                    AppManagerData(
                        name = existing?.name ?: File(path).nameWithoutExtension,
                        packageName = path,
                        icon = path,
                        isSystemApp = false,
                        isChecked = true
                    )
                }
            }
        }
    }

    override fun onSelectFile() {
        pickExeFile()?.let { path ->
            onAddApp(
                AppManagerData(
                    name = File(path).nameWithoutExtension,
                    packageName = path,
                    icon = path,
                    isSystemApp = false,
                    isChecked = true
                )
            )
        }
    }
}

actual fun getSplitTunnelViewModel(): ISplitTunnelViewModel =
    SplitTunnelViewModelDesktop()