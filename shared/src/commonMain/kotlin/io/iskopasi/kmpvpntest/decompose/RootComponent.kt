package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import io.iskopasi.kmpvpntest.api.getSplitTunnelComponent
import org.koin.core.component.KoinComponent

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {
    val main = MainComponentImpl(
        componentContext = childContext("main"),
    )

    val splitTunnel = getSplitTunnelComponent(context = childContext("split_tunnel"))
}
