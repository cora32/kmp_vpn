package io.iskopasi.kmpvpntest.api

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.StateFlow

interface PermissionsApi {
    val isVPNGrantedFlow: StateFlow<Boolean>
    val isNotificationGrantedFlow: StateFlow<Boolean>
    val requestVPNPermission: MutableState<Boolean>
    val requestPostPermission: MutableState<Boolean>

    fun setVPNPermissionState(isGranted: Boolean)
    fun setNotificationPermissionState(isGranted: Boolean)
    fun requestPermissions(): Pair<Boolean, Boolean>
}

abstract class PermissionsApiDefault : PermissionsApi {
    override fun setVPNPermissionState(isGranted: Boolean) {
        "[PermissionsApi] VPN permission: $isGranted".e
    }

    override fun setNotificationPermissionState(isGranted: Boolean) {
        "[PermissionsApi] Post notification permission: $isGranted".e
    }
}


expect fun getPermissionApi(): PermissionsApi