package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.api.PermissionsApi
import io.iskopasi.kmpvpntest.e
import io.iskopasi.kmpvpntest.managers.VPNService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    data class State(
        val isConnected: Boolean = false
    )
}

class MainComponentImpl(
    private val componentContext: ComponentContext,
    private val vpnRepo: VPNService,
    private val permissionApi: PermissionsApi,
) : MainComponent, ComponentContext by componentContext {
    private val _state = MutableStateFlow(MainComponent.State())
    private val _host = MutableStateFlow("")
    private val _port = MutableStateFlow("")
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    override val state = _state.asStateFlow()
    override val host = _host.asStateFlow()
    override val port = _port.asStateFlow()
    override val username = _username.asStateFlow()
    override val password = _password.asStateFlow()

    override fun onConnect() {
        val (isVPNGranted, isNotificationGranted)
                = permissionApi.requestPermissions()

        "[MainComponentImpl] isVPNGranted: $isVPNGranted; isNotificationGranted: $isNotificationGranted".e

        if (isVPNGranted && isNotificationGranted) {
            _state.update {
                it.copy(
                    isConnected = !it.isConnected
                )
            }
        }
    }

    override fun onHostChanged(value: String) {
        _host.update {
            value
        }
    }

    override fun onPortChanged(value: String) {
        _port.update {
            value
        }
    }

    override fun onUsernameChanged(value: String) {
        _username.update {
            value
        }
    }

    override fun onPasswordChanged(value: String) {
        _password.update {
            value
        }
    }

    fun onVPNPermissionGranted() = permissionApi.setVPNPermissionState(isGranted = true)
    fun onVPNPermissionDenied() = permissionApi.setVPNPermissionState(isGranted = false)

    fun onPostPermissionGranted() = permissionApi.setNotificationPermissionState(isGranted = true)
    fun onPostPermissionDenied() = permissionApi.setNotificationPermissionState(isGranted = false)
}