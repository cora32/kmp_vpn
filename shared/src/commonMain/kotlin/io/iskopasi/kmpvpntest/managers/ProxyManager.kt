package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProxyManager(
    val vpnApi: VPNLauncherInterface,
    val prefStorage: PrefStoreApi,
    val signalManager: SignalManager,
) : KoinComponent {
    private val proxyApi: ProxyApi by inject()
    private var lastFetchTimestamp = 0L
    private var tempList = emptyList<ProxyData>()

    var proxyData: ProxyData
        get() = prefStorage.proxyData
        set(value) {
            prefStorage.proxyData = value
        }

    var isAuthEnabled: Boolean
        get() = prefStorage.isAuthEnabled
        set(value) {
            prefStorage.isAuthEnabled = value
        }


    fun startVPN() {
        vpnApi.startVPN(isAuthEnabled = isAuthEnabled)
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

    suspend fun checkConnection(isCertCheckEnabled: Boolean): Boolean = proxyApi.connect(
        proxyData = proxyData,
        isCertCheckEnabled = isCertCheckEnabled
    )

    suspend fun fetchProxyList(): List<ProxyData> {
        if (lastFetchTimestamp < 5 * 60 * 1000 || tempList.isEmpty()) {
            lastFetchTimestamp = System.currentTimeMillis()
            tempList = proxyApi.fetchProxyList()
        }

        return tempList.shuffled().take(20)
    }
}
