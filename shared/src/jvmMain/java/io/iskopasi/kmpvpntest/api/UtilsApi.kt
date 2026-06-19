package io.iskopasi.kmpvpntest.api

actual fun logError(msg: String) {
    System.err.println("--> $msg")
}

actual val isAndroid: Boolean = false
