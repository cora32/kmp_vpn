package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.api.PermissionsApi
import io.iskopasi.kmpvpntest.e
import io.iskopasi.kmpvpntest.managers.ProxyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface MainComponent {
    val state: StateFlow<State>
    val host: StateFlow<String>
    val port: StateFlow<String>
    val username: StateFlow<String>
    val password: StateFlow<String>

    fun onConnect()
    fun onHostChanged(value: String)
    fun onPortChanged(value: String)
    fun onUsernameChanged(value: String)
    fun onPasswordChanged(value: String)

    enum class State {
        Idle,
        Connecting,
        Connected
    }
}

class MainComponentImpl(
    private val componentContext: ComponentContext,
    private val proxyManager: ProxyManager,
    private val permissionApi: PermissionsApi,
) : MainComponent, ComponentContext by componentContext {
    private val _state = MutableStateFlow(MainComponent.State.Idle)
    private val _host = MutableStateFlow(proxyManager.proxyData.host)
    private val _port = MutableStateFlow(proxyManager.proxyData.port)
    private val _username = MutableStateFlow(proxyManager.proxyData.username)
    private val _password = MutableStateFlow(proxyManager.proxyData.password)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val state = _state.asStateFlow()
    override val host = _host.asStateFlow()
    override val port = _port.asStateFlow()
    override val username = _username.asStateFlow()
    override val password = _password.asStateFlow()

    override fun onConnect() {
        permissionApi.requestPermissions()

        scope.launch {
            combine(
                permissionApi.isVPNGrantedFlow,
                permissionApi.isNotificationGrantedFlow
            ) { isVPNGranted, isNotificationGranted ->
                isVPNGranted && isNotificationGranted
            }.collect { bothGranted ->
                if (bothGranted) {
                    // Decide whether to connect or disconnect based on CURRENT state
                    if (state.value == MainComponent.State.Connected) {
                        disconnect()
                    } else if (state.value == MainComponent.State.Idle) {
                        connect()
                    }
                }
            }
        }
    }

    private fun disconnect() {
        proxyManager.stopVPN()
        proxyManager.signalManager.reset()
        setState(MainComponent.State.Idle)
    }

    private fun connect() {
        setState(MainComponent.State.Connecting)

        scope.launch {
            proxyManager.signalManager.reset()
            proxyManager.startVPN()

            "[MainComponent] waiting for isConnected...".e
            val isConnected = proxyManager.isConnected()
            "[MainComponent] isConnected result: $isConnected".e

            if (isConnected) {
                setState(MainComponent.State.Connected)
            } else {
                setState(MainComponent.State.Idle)
            }
        }
    }

    private fun setState(state: MainComponent.State) {
        "[MainComponent] Setting state: $state".e

        _state.update {
            state
        }
    }

    override fun onHostChanged(value: String) {
        _host.update {
            value
        }

        proxyManager.setHost(value)
    }

    override fun onPortChanged(value: String) {
        _port.update {
            value
        }

        proxyManager.setPort(value)
    }

    override fun onUsernameChanged(value: String) {
        _username.update {
            value
        }

        proxyManager.setUsername(value)
    }

    override fun onPasswordChanged(value: String) {
        _password.update {
            value
        }

        proxyManager.setPassword(value)
    }

    fun onVPNPermissionGranted() = permissionApi.setVPNPermissionState(isGranted = true)
    fun onVPNPermissionDenied() = permissionApi.setVPNPermissionState(isGranted = false)

    fun onPostPermissionGranted() = permissionApi.setNotificationPermissionState(isGranted = true)
    fun onPostPermissionDenied() = permissionApi.setNotificationPermissionState(isGranted = false)
}