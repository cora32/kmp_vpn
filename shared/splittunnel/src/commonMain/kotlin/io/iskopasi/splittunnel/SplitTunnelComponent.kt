package io.iskopasi.splittunnel

import com.arkivanov.decompose.value.Value

interface SplitTunnelComponent {
    val model: Value<Model>

    fun onAddApp(path: String)
    fun onRemoveApp(path: String)
    fun onSelectFile()
    fun onRefreshProcesses()

    data class Model(
        val selectedApps: List<String> = emptyList(),
        val runningProcesses: List<String> = emptyList()
    )
}
