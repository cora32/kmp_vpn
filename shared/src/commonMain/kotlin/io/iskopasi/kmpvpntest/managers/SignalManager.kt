package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.e
import kotlinx.coroutines.flow.MutableSharedFlow

class SignalManager {
    val signalBus =
        MutableSharedFlow<Boolean?>(replay = 1)

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
}