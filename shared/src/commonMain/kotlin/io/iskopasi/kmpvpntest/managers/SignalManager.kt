package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.e
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class SignalManager {
    val signalBus =
        MutableSharedFlow<Boolean?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val errorBus = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun onConnected() {
        "[Signal] onConnected".e
        signalBus.tryEmit(true)
    }

    fun onDisconnected() {
        "[Signal] onDisconnected".e
        signalBus.tryEmit(false)
    }

    fun reset() {
        signalBus.tryEmit(null)
        errorBus.tryEmit("")
    }

    fun onError(message: String) {
        "[Signal] onError: $message".e
        errorBus.tryEmit(message)
    }
}