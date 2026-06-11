package io.iskopasi.kmpvpntest

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.iskopasi.kmpvpntest.decompose.RootComponent
import io.iskopasi.kmpvpntest.di.getModules
import io.iskopasi.kmpvpntest.managers.VPNLauncher
import io.iskopasi.kmpvpntest.managers.VPNLauncherInterface
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.File


fun isWindowsAdmin(): Boolean {
    return try {
        // The 'net session' command only succeeds if running as Admin
        val process = Runtime.getRuntime().exec("net session")
        process.waitFor() == 0
    } catch (e: Exception) {
        false
    }
}

fun elevateProcess() {
    val javaBin =
        System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe"
    // Get the path of the currently running JAR or class path
    val classpath = System.getProperty("java.class.path")
    val mainClass = "io.iskopasi.kmpvpntest.MainKt"

    // Use PowerShell to trigger the UAC prompt
    val pb = ProcessBuilder(
        "powershell",
        "Start-Process",
        "'$javaBin'",
        "-ArgumentList",
        "'-cp \"$classpath\" $mainClass'",
        "-Verb", "RunAs"
    )

    // Inherit standard IO so you can see outputs if they occur
    pb.inheritIO()
    pb.start()
}

fun main() = application {
    if (!isWindowsAdmin()) {
        elevateProcess()
        return@application // Exit the non-admin process
    }

    startKoin {
        modules(
            getModules(),

            module {
                single<VPNLauncherInterface> {
                    VPNLauncher()
                }
            }
        )
    }

    val model = RootComponent(
        componentContext = DefaultComponentContext(
            LifecycleRegistry()
        ),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "kmpvpntest",
    ) {
        App(model = model.main)
    }
}