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
        scope.launch {
            if (value)
                prefStore.allowedApps += packageName
            else
                prefStore.allowedApps -= packageName

            onAppListChanged(prefStore.allowedApps)

            resortAppList(showSystemApp = _showSystemAppsFlow.value)
        }
    }
}

actual fun getSplitTunnelComponent(context: ComponentContext): SplitTunnelComponent =
    SplitTunnelComponentAndroid(context)