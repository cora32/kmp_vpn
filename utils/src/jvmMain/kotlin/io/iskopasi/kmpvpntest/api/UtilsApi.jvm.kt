package io.iskopasi.kmpvpntest.api

actual fun logError(msg: String) {
    System.err.println("--> $msg")
}

actual fun showToast(msg: String) {
    println("[TOAST] $msg")
}

actual val isAndroid: Boolean = false
