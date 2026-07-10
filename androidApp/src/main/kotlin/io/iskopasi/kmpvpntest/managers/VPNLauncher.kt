package io.iskopasi.kmpvpntest.managers

import android.app.Application
import android.content.Intent
import android.os.Build
import io.iskopasi.kmpvpntest.HostExtra
import io.iskopasi.kmpvpntest.LogLevelExtra
import io.iskopasi.kmpvpntest.PasswordExtra
import io.iskopasi.kmpvpntest.PortExtra
import io.iskopasi.kmpvpntest.StartCommand
import io.iskopasi.kmpvpntest.StopCommand
import io.iskopasi.kmpvpntest.UsernameExtra
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.e
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VPNLauncher : VPNLauncherInterface, KoinComponent {
    private val application: Application by inject()
    private val prefStore: PrefStoreApi by inject()

    override fun startVPN(isAuthEnabled: Boolean) {
        val proxyData = prefStore.proxyData
        // We use string literals for intent actions and extras because the Android constants
        // are in the :androidApp module which is not visible to :shared
        val intent = Intent().apply {
            setClassName(application.packageName, "io.iskopasi.kmpvpntest.services.VPNServiceImpl")
            action = StartCommand
            putExtra(HostExtra, proxyData.host)
            putExtra(PortExtra, proxyData.port)
            if (isAuthEnabled) {
                putExtra(UsernameExtra, proxyData.username)
                putExtra(PasswordExtra, proxyData.password)
            }
            putExtra(LogLevelExtra, "debug")
        }

        "[VPNLauncher] Starting foreground service...".e
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }

    override fun stopVPN() {
        val intent = Intent().apply {
            setClassName(application.packageName, "io.iskopasi.kmpvpntest.services.VPNServiceImpl")
            action = StopCommand
        }
        application.startService(intent)
    }

}