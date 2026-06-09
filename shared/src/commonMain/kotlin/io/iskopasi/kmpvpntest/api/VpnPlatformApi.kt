package io.iskopasi.kmpvpntest.api

interface VpnPlatformApi {
    fun startVPN()

    fun stopVPN()
}

expect fun getVpnPlatformApi(): VpnPlatformApi