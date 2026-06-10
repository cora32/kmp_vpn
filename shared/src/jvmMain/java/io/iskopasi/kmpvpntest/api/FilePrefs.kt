package io.iskopasi.kmpvpntest.api

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class FilePrefs(fileName: String = "prefs.properties") {
    private val props = Properties()
    private val appFolder = File(System.getProperty("user.home"), ".kmpvpn")
    private val prefFile = File(appFolder, fileName)

    init {
        if (!appFolder.exists()) appFolder.mkdirs()
        if (prefFile.exists()) {
            FileInputStream(prefFile).use { props.load(it) }
        }
    }

    private fun save() {
        FileOutputStream(prefFile).use { props.store(it, "KMP VPN Preferences") }
    }

    fun put(key: String, value: String) {
        props.setProperty(key, value)
        save()
    }

    fun get(key: String, defaultValue: String): String {
        return props.getProperty(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        props.setProperty(key, value.toString())
        save()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return props.getProperty(key, defaultValue.toString()).toBoolean()
    }

    fun remove(key: String) {
        props.remove(key)
        save()
    }
}
