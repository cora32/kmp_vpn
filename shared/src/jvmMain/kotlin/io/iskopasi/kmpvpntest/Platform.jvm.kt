package io.iskopasi.kmpvpntest

class JVMVpnPlatformApi: VpnPlatformApi {
    override fun startVPN() {
        TODO("Not yet implemented")
    }

    override fun stopVPN() {
        TODO("Not yet implemented")
    }
}

actual fun getVpnPlatformApi(): VpnPlatformApi = JVMVpnPlatformApi()