package io.iskopasi.kmpvpntest.di

import io.iskopasi.kmpvpntest.api.PermissionsApi
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.getPermissionApi
import io.iskopasi.kmpvpntest.api.getPrefStore
import io.iskopasi.kmpvpntest.managers.PingApi
import io.iskopasi.kmpvpntest.managers.PingApiImpl
import io.iskopasi.kmpvpntest.managers.ProxyManager
import io.iskopasi.kmpvpntest.managers.SignalManager
import io.iskopasi.splittunnel.managers.AppManager
import io.iskopasi.splittunnel.managers.getAppManager
import org.koin.dsl.module

fun getModules() = module {
    factory<ProxyManager> {
        ProxyManager(
            vpnApi = get(),
            prefStorage = get(),
            signalManager = get()
        )
    }
    single<PrefStoreApi> {
        getPrefStore()
    }
    single<PermissionsApi> {
        getPermissionApi()
    }
    single<SignalManager> {
        SignalManager()
    }
    factory<PingApi> {
        PingApiImpl()
    }
    factory<AppManager> {
        getAppManager()
    }
}