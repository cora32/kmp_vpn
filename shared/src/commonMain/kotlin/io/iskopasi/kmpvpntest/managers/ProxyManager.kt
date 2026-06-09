package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
data class ProxyData(
    val host: String,
    val port: String,
    val username: String,
    val password: String
) {
    companion object {
        val empty = ProxyData(
            host = "",
            port = "",
            username = "",
            password = ""
        )
    }
}

class ProxyManager(
    val vpnApi: VPNLauncherInterface,
    val prefStorage: PrefStoreApi,
    val signalManager: SignalManager,
) {
    var proxyData: ProxyData
        get() = prefStorage.proxyData
        set(value) {
            prefStorage.proxyData = value
        }

    fun startVPN() {
        vpnApi.startVPN()
    }

    fun stopVPN() {
        vpnApi.stopVPN()
    }

    fun setHost(value: String) {
        proxyData = proxyData.copy(
            host = value
        )
    }

    fun setPort(value: String) {
        proxyData = proxyData.copy(
            port = value
        )
    }

    fun setUsername(value: String) {
        proxyData = proxyData.copy(
            username = value
        )
    }

    fun setPassword(value: String) {
        proxyData = proxyData.copy(
            password = value
        )
    }

    suspend fun isConnected(): Boolean {
        return signalManager.signalBus.first { it != null } == true
    }
}