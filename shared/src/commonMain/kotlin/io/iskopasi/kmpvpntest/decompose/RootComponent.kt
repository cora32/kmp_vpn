package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import io.iskopasi.dns_filter.decompose.DnsFilterComponent
import io.iskopasi.kmpvpntest.api.EventBus
import io.iskopasi.kmpvpntest.api.getSplitTunnelComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {
    val eventBus: EventBus by inject()

    val main = MainComponentImpl(
        componentContext = childContext("main"),
    )
    val splitTunnel = getSplitTunnelComponent(context = childContext("split_tunnel"))
    val dnsFilterComponent = DnsFilterComponent(context = childContext("dns_filter"))
}
