package io.iskopasi.kmpvpntest.api

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DesktopPermissionApi : PermissionsApiDefault() {
    private val _isVPNGrantedFlow = MutableStateFlow<Boolean>(true)
    private val _isNotificationGrantedFlow = MutableStateFlow<Boolean>(true)

    override val isVPNGrantedFlow = _isVPNGrantedFlow.asStateFlow()
    override val isNotificationGrantedFlow = _isNotificationGrantedFlow.asStateFlow()
    override val requestVPNPermission: MutableState<Boolean> = mutableStateOf(false)
    override val requestPostPermission: MutableState<Boolean> = mutableStateOf(false)

    override fun setVPNPermissionState(isGranted: Boolean) {
        // Noop
    }

    override fun setNotificationPermissionState(isGranted: Boolean) {
        // Noop
    }

    override fun requestPermissions(): Pair<Boolean, Boolean> {
        return isVPNGrantedFlow.value to isNotificationGrantedFlow.value
    }

}

actual fun getPermissionApi(): PermissionsApi = DesktopPermissionApi()