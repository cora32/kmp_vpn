package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.PermissionsApi
import io.iskopasi.kmpvpntest.e
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
    private val componentContext: ComponentContext,
    private val vpnRepo: VPNRepo,
    private val permissionApi: PermissionsApi,
) : MainComponent, ComponentContext by componentContext {
    private val _state = MutableStateFlow(MainComponent.State())

    override val state = _state.asStateFlow()

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

    fun onVPNPermissionGranted() = permissionApi.setVPNPermissionState(isGranted = true)
    fun onVPNPermissionDenied() = permissionApi.setVPNPermissionState(isGranted = false)

    fun onPostPermissionGranted() = permissionApi.setNotificationPermissionState(isGranted = true)
    fun onPostPermissionDenied() = permissionApi.setNotificationPermissionState(isGranted = false)
}