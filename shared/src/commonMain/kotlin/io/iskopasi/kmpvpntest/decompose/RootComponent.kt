package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import org.koin.core.Koin

class RootComponent(
    componentContext: ComponentContext,
    koin: Koin
): ComponentContext by componentContext {
    val main = MainComponentImpl(
        componentContext = childContext("main"),
        vpnRepo = koin.get()
    )
}