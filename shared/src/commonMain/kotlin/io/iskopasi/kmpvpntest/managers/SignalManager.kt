package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.e
import kotlinx.coroutines.flow.MutableSharedFlow

class SignalManager {
    val signalBus =
        MutableSharedFlow<Boolean?>(replay = 1)
    val errorBus = MutableSharedFlow<String>(replay = 0) // New error flow

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
    }

    fun onError(message: String) {
        errorBus.tryEmit(message)
    }
}