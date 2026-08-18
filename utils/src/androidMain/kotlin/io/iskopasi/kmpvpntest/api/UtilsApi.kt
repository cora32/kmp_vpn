package io.iskopasi.kmpvpntest.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.koin.core.context.GlobalContext

actual fun logError(msg: String) {
    Log.e("-->", msg)
}

actual fun showToast(msg: String) {
    val context = GlobalContext.get().get<Context>()
    "--> Toasting: $msg; $context".e
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

actual val isAndroid: Boolean = true
