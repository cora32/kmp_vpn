package io.iskopasi.kmpvpntest.api

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent

expect fun getSplitTunnelComponent(context: ComponentContext): SplitTunnelComponent