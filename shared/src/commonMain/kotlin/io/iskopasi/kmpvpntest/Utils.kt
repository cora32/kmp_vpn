package io.iskopasi.kmpvpntest

val String.e: String
    get() {
        logError(this)
        return this
    }