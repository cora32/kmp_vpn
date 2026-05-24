package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.VpnPlatformApi

interface VPNRepo {
    fun connect()

    fun disconnect()
}

class VPNRepoImpl(
    val vpnPlatformApi: VpnPlatformApi
) : VPNRepo {
    override fun connect() {
        vpnPlatformApi.startVPN()
    }

    override fun disconnect() {
        vpnPlatformApi.stopVPN()
    }
}