package io.iskopasi.kmpvpntest.api

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent
import io.iskopasi.splittunnel.decompose.SplitTunnelComponentAbstract
import io.iskopasi.splittunnel.getRunningProcesses
import io.iskopasi.splittunnel.managers.AppManagerData
import io.iskopasi.splittunnel.pickExeFile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.io.File

class SplitTunnelComponentDesktop(
    componentContext: ComponentContext
) : SplitTunnelComponentAbstract(), ComponentContext by componentContext, KoinComponent {

    init {
        scope.launch {
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
        scope.launch {
            val processList = getRunningProcesses()

            _runningProcessesFlow.update {
                processList
            }
        }
    }

    override fun onAddApp(data: AppManagerData) {
        scope.launch {
            data.isChecked = true
            prefStore.allowedApps += data.packageName

            resortAppList(showSystemApp = true)

            refreshAllowedFlow()
        }
    }

    override fun onRemoveApp(data: AppManagerData) {
        scope.launch {
            data.isChecked = false
            prefStore.allowedApps -= data.packageName

            resortAppList(showSystemApp = true)

            refreshAllowedFlow()
        }
    }

    private fun refreshAllowedFlow() {
        scope.launch {
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

actual fun getSplitTunnelComponent(context: ComponentContext): SplitTunnelComponent =
    SplitTunnelComponentDesktop(context)