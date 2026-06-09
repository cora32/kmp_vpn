package io.iskopasi.kmpvpntest

import android.app.Application
import io.iskopasi.kmpvpntest.api.getPrefStore
import io.iskopasi.kmpvpntest.api.getVpnPlatformApi
import io.iskopasi.kmpvpntest.di.getModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                getModel(
                    vpnPlatformApi = getVpnPlatformApi(),
                    prefStoreApi = getPrefStore()
                )
            )
        }
    }
}