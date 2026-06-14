package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.splittunnel.SplitTunnelComponentImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {
    private val prefStore: PrefStoreApi by inject()

    val main = MainComponentImpl(
        componentContext = childContext("main"),
    )

    val splitTunnel = SplitTunnelComponentImpl(
        componentContext = childContext("splitTunnel"),
        initialApps = prefStore.allowedApps,
        onAppListChanged = { apps ->
            if (apps.isEmpty()) {
                prefStore.allowAllApps = true
                prefStore.allowedApps = emptySet()
            } else {
                prefStore.allowAllApps = false
                prefStore.allowedApps = apps
            }
        }
    )
}
