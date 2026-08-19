package io.iskopasi.kmpvpntest.managers

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual fun getPermissionRequester(): IPermissionRequester = object : IPermissionRequester {
    private val _requestFlow = MutableSharedFlow<PermissionType>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val requestFlow = _requestFlow.asSharedFlow()

    override fun request(type: PermissionType) {
        val success = _requestFlow.tryEmit(type)
    }
}