package io.iskopasi.kmpvpntest

import io.iskopasi.kmpvpntest.api.logError

val String.e: String
    get() {
        logError(this)
        return this
    }