package io.iskopasi.kmpvpntest

import android.app.Application
import io.iskopasi.kmpvpntest.api.AppContext
import io.iskopasi.kmpvpntest.di.getModules
import io.iskopasi.kmpvpntest.managers.NManager
import io.iskopasi.kmpvpntest.managers.VPNLauncher
import io.iskopasi.kmpvpntest.managers.VPNLauncherInterface
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        startKoin {
            androidContext(this@App)
            modules(
                getModules(),

                module {
                    factory<NManager> {
                        NManager(application = get())
                    }
                    factory<VPNLauncherInterface> {
                        VPNLauncher()
                    }
                }
            )
        }
    }
}