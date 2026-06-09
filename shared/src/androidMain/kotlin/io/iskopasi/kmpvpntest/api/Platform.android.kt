package io.iskopasi.kmpvpntest.api

class AndroidVpnPlatformApi : VpnPlatformApi {
    override fun startVPN() {
        TODO("Not yet implemented")
    }

    override fun stopVPN() {
        TODO("Not yet implemented")
    }
}

actual fun getVpnPlatformApi(): VpnPlatformApi = AndroidVpnPlatformApi()