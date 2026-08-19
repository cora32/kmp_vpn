package io.iskopasi.kmpvpntest.di

import io.iskopasi.kmpvpntest.api.EventBus
import io.iskopasi.kmpvpntest.api.PermissionsApi
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.getPermissionApi
import io.iskopasi.kmpvpntest.api.getPrefStore
import io.iskopasi.kmpvpntest.managers.FilterDao
import io.iskopasi.kmpvpntest.managers.IPermissionRequester
import io.iskopasi.kmpvpntest.managers.ProxyApi
import io.iskopasi.kmpvpntest.managers.ProxyApiImpl
import io.iskopasi.kmpvpntest.managers.ProxyManager
import io.iskopasi.kmpvpntest.managers.SignalManager
import io.iskopasi.kmpvpntest.managers.getDatabaseBuilder
import io.iskopasi.kmpvpntest.managers.getPermissionRequester
import io.iskopasi.kmpvpntest.managers.getRoomDatabase
import io.iskopasi.kmpvpntest.viewmodels.HomeViewModel
import io.iskopasi.splittunnel.managers.AppManager
import io.iskopasi.splittunnel.managers.getAppManager
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun getModules() = module {
    single<EventBus> {
        EventBus()
    }
    single<FilterDao> {
        getRoomDatabase(getDatabaseBuilder()).getFilterDao()
    }
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
    single<IPermissionRequester> {
        getPermissionRequester()
    }
    single<SignalManager> {
        SignalManager()
    }
    factory<ProxyApi> {
        ProxyApiImpl()
    }
    factory<AppManager> {
        getAppManager()
    }
    // ViewModels
    viewModelOf(::HomeViewModel)
}