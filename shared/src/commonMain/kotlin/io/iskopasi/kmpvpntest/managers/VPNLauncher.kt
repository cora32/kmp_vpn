package io.iskopasi.kmpvpntest.managers

interface VPNLauncherInterface {
    fun startVPN(isAuthEnabled: Boolean)
    fun stopVPN()
}