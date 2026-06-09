package io.iskopasi.kmpvpntest.api

interface PrefStoreApi {
    var allowedApps: Set<String>
    var allowAllApps: Boolean
}

expect fun getPrefStore(): PrefStoreApi