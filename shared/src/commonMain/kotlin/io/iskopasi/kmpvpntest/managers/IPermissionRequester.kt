package io.iskopasi.kmpvpntest.managers

import kotlinx.coroutines.flow.SharedFlow

enum class PermissionType {
    Notification,
    Vpn
}

interface IPermissionRequester {
    fun request(type: PermissionType)
    val requestFlow: SharedFlow<PermissionType>
}

expect fun getPermissionRequester(): IPermissionRequester