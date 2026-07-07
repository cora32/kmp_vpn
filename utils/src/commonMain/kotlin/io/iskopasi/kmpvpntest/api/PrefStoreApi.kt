package io.iskopasi.kmpvpntest.api

import io.iskopasi.kmpvpntest.managers.ProxyData
import org.koin.core.component.KoinComponent

interface PrefStoreApi : KoinComponent {
    var proxyData: ProxyData
    var allowedApps: Set<String>
    var allowedAppsNamesOnly: Set<String>
    var routeAllApps: Boolean
    var showSystemApps: Boolean
    var isAuthEnabled: Boolean
    var isCertCheckEnabled: Boolean
}

expect fun getPrefStore(): PrefStoreApi
