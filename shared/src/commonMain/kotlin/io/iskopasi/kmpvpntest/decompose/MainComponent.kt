package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.managers.VPNRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface MainComponent {
    val state: StateFlow<State>

    fun onConnect()

    data class State(
        val isConnected: Boolean = false
    )
}

class MainComponentImpl(
    val componentContext: ComponentContext,
    val vpnRepo: VPNRepo
) : MainComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(MainComponent.State())

    override val state = _state.asStateFlow()

    override fun onConnect() {
        _state.update {
            it.copy(
                isConnected = !it.isConnected
            )
        }
    }
}