package io.iskopasi.kmpvpntest.di

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.VpnPlatformApi
import io.iskopasi.kmpvpntest.managers.VPNService
import org.koin.dsl.module

fun getModel(
    vpnPlatformApi: VpnPlatformApi,
    prefStoreApi: PrefStoreApi
) = module {
    single<VPNService> {
        VPNService(
            vpnPlatformApi = vpnPlatformApi,
        )
    }
    single<PrefStoreApi> {
        prefStoreApi
    }
}