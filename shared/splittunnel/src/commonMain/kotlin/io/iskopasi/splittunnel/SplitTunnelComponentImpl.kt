package io.iskopasi.splittunnel

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

class SplitTunnelComponentImpl(
    componentContext: ComponentContext,
    private val initialApps: Set<String>,
    private val onAppListChanged: (Set<String>) -> Unit
) : SplitTunnelComponent, ComponentContext by componentContext {

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
}
