package io.iskopasi.kmpvpntest

actual fun logError(msg: String) {
    System.err.println("--> $msg")
}