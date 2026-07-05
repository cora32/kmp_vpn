package io.iskopasi.kmpvpntest.api

import android.util.Log
import android.widget.Toast

actual fun logError(msg: String) {
    Log.e("-->", msg)
}

actual fun showToast(msg: String) {
    "--> Toasting: $msg; ${AppContext.get()}".e
    Toast.makeText(AppContext.get(), msg, Toast.LENGTH_SHORT).show()
}

actual val isAndroid: Boolean = true
