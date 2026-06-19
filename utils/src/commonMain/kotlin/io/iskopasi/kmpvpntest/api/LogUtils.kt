package io.iskopasi.kmpvpntest.api

val String.e: String
    get() {
        logError(this)
        return this
    }
