package io.iskopasi.kmpvpntest.managers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual fun getPermissionRequester(): IPermissionRequester = object : IPermissionRequester {
    private val _requestFlow = MutableSharedFlow<PermissionType>()
    override val requestFlow: SharedFlow<PermissionType> = _requestFlow.asSharedFlow()

    override fun request(type: PermissionType) {

    }
}