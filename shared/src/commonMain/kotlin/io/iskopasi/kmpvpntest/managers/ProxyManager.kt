package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
) : KoinComponent {
    private val pingApi: PingApi by inject()

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

    suspend fun checkConnection(): Boolean = pingApi.connect(proxyData = proxyData)
}