package io.iskopasi.splittunnel

import io.iskopasi.splittunnel.managers.AppManagerData
import java.io.File
import kotlin.streams.asSequence

actual fun getRunningProcesses(): List<AppManagerData> {
    return ProcessHandle.allProcesses()
        .asSequence()
        .map { it.info().command() }
        .filter { it.isPresent }
        .map { it.get() }
        .filter { it.isNotBlank() }
        .map { path ->
            val name = File(path).nameWithoutExtension
            AppManagerData(
                name = name,
                packageName = path,
                isSystemApp = false,
                isChecked = false,
                icon = path // Path to exe serves as icon source for Coil on Windows
            )
        }
        .distinctBy { it.packageName }
        .sortedBy { it.name.lowercase() }
        .toList()
}
