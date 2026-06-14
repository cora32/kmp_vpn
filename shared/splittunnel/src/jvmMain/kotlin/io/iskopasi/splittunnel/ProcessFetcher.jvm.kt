package io.iskopasi.splittunnel

import java.io.BufferedReader
import java.io.InputStreamReader

actual fun getRunningProcesses(): List<String> {
    val processes = mutableSetOf<String>()
    try {
        val process = ProcessBuilder("tasklist", "/NH", "/FO", "CSV").start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val parts = line?.split(",") ?: continue
            if (parts.isNotEmpty()) {
                val name = parts[0].replace("\"", "").trim()
                if (name.isNotEmpty()) {
                    processes.add(name)
                }
            }
        }
        process.waitFor()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return processes.toList().sortedBy { it.lowercase() }
}
