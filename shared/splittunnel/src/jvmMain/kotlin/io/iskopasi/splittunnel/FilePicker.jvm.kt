package io.iskopasi.splittunnel

import java.awt.FileDialog
import java.awt.Frame

actual fun pickExeFile(): String? {
    val dialog = FileDialog(null as Frame?, "Select Executable", FileDialog.LOAD)
    dialog.setFilenameFilter { _, name -> name.endsWith(".exe") }
    dialog.isVisible = true
    return if (dialog.file != null) {
        dialog.directory + dialog.file
    } else {
        null
    }
}
