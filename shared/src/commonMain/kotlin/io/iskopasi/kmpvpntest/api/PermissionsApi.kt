package io.iskopasi.kmpvpntest.api

import io.iskopasi.kmpvpntest.managers.IPermissionRequester
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface PermissionsApi {
    val isVPNGrantedFlow: StateFlow<Boolean>
    val isNotificationGrantedFlow: StateFlow<Boolean>
    val permissionRequester: IPermissionRequester

    fun setVPNPermissionState(isGranted: Boolean)
    fun setNotificationPermissionState(isGranted: Boolean)
    fun requestPermissions(): Pair<Boolean, Boolean>
}

abstract class PermissionsApiDefault : PermissionsApi, KoinComponent {
    override val permissionRequester by inject<IPermissionRequester>()

    override fun setVPNPermissionState(isGranted: Boolean) {
        "[PermissionsApi] VPN permission: $isGranted".e
    }

    override fun setNotificationPermissionState(isGranted: Boolean) {
        "[PermissionsApi] Post notification permission: $isGranted".e
    }
}


expect fun getPermissionApi(): PermissionsApi