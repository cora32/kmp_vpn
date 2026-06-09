package io.iskopasi.kmpvpntest

import android.app.Application
import io.iskopasi.kmpvpntest.api.getPrefStore
import io.iskopasi.kmpvpntest.di.getModel
import io.iskopasi.kmpvpntest.managers.NManager
import io.iskopasi.kmpvpntest.managers.VPNLauncher
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                getModel(
                    prefStoreApi = getPrefStore(),
                    vpnLauncher = VPNLauncher()
                ),

                module {
                    single<NManager> {
                        NManager(application = get())
                    }
                }
            )
        }
    }
}