package io.iskopasi.kmpvpntest

interface VpnPlatformApi {
    fun startVPN()

    fun stopVPN()
}

expect fun getVpnPlatformApi(): VpnPlatformApi