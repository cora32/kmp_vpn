package io.iskopasi.kmpvpntest.api

import io.iskopasi.kmpvpntest.managers.ProxyData

interface PrefStoreApi {
    var proxyData: ProxyData
    var allowedApps: Set<String>
    var allowAllApps: Boolean
}

expect fun getPrefStore(): PrefStoreApi