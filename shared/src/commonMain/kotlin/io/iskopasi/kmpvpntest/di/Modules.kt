package io.iskopasi.kmpvpntest.di

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.managers.ProxyManager
import io.iskopasi.kmpvpntest.managers.SignalManager
import io.iskopasi.kmpvpntest.managers.VPNLauncherInterface
import org.koin.dsl.module

fun getModel(
    prefStoreApi: PrefStoreApi,
    vpnLauncher: VPNLauncherInterface
) = module {
    single<ProxyManager> {
        ProxyManager(
            vpnApi = get(),
            prefStorage = prefStoreApi,
            signalManager = get()
        )
    }
    single<PrefStoreApi> {
        prefStoreApi
    }
    single<SignalManager> {
        SignalManager()
    }
    single<VPNLauncherInterface> {
        vpnLauncher
    }
}