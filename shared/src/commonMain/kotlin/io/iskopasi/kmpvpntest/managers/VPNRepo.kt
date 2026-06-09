package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.VpnPlatformApi

class VPNService(
    val vpnPlatformApi: VpnPlatformApi
) {
    fun connect() {
        vpnPlatformApi.startVPN()
    }

    fun disconnect() {
        vpnPlatformApi.stopVPN()
    }
}