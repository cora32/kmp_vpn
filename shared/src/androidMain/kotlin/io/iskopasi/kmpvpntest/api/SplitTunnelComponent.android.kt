package io.iskopasi.kmpvpntest.api

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent
import io.iskopasi.splittunnel.decompose.SplitTunnelComponentAbstract
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SplitTunnelComponentAndroid(
    componentContext: ComponentContext
) : SplitTunnelComponentAbstract(), ComponentContext by componentContext, KoinComponent {
    override fun onCheckApp(packageName: String, value: Boolean) {
        allowedAppsMap[packageName] = value
        onAppListChanged(allowedAppsMap.filterValues { it }.keys)

        scope.launch {
            resortAppList(showSystemApp = _showSystemAppsFlow.value)
        }
    }
}

actual fun getSplitTunnelComponent(context: ComponentContext): SplitTunnelComponent =
    SplitTunnelComponentAndroid(context)