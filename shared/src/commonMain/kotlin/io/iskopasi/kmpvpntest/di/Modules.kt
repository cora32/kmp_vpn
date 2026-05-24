package io.iskopasi.kmpvpntest.di

import io.iskopasi.kmpvpntest.VpnPlatformApi
import io.iskopasi.kmpvpntest.managers.VPNRepo
import io.iskopasi.kmpvpntest.managers.VPNRepoImpl
import org.koin.dsl.module

fun getModel(vpnPlatformApi: VpnPlatformApi) = module {
    single<VPNRepo> {
        VPNRepoImpl(
            vpnPlatformApi = vpnPlatformApi
        )
    }
}