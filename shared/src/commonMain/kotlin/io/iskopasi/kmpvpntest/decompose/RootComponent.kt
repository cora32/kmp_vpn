package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import io.iskopasi.kmpvpntest.api.PermissionsApi
import org.koin.core.Koin

class RootComponent(
    componentContext: ComponentContext,
    koin: Koin,
    permissionApi: PermissionsApi
): ComponentContext by componentContext {
    val main = MainComponentImpl(
        componentContext = childContext("main"),
        vpnRepo = koin.get(),
        permissionApi = permissionApi
    )
}