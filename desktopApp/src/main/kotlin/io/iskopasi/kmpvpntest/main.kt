package io.iskopasi.kmpvpntest

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.iskopasi.kmpvpntest.api.initializeCoil
import io.iskopasi.kmpvpntest.decompose.RootComponent
import io.iskopasi.kmpvpntest.di.getModules
import io.iskopasi.kmpvpntest.managers.SingleInstanceManager
import io.iskopasi.kmpvpntest.managers.VPNLauncher
import io.iskopasi.kmpvpntest.managers.VPNLauncherInterface
import io.iskopasi.kmpvpntest.theme.MaterialIconsMinimize
import io.iskopasi.kmpvpntest.theme.VscodeCodiconsClose
import io.iskopasi.kmpvpntest.theme.dark
import io.iskopasi.kmpvpntest.theme.light
import org.koin.core.context.GlobalContext
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
        System.getProperty("java.home") + File.separator + "bin" + File.separator + JavaExe
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
    // 1. State to track focus requests (using timestamp to ensure change detection)
    var focusTrigger by remember { mutableStateOf(0L) }
    val windowState = rememberWindowState(width = 500.dp, height = 800.dp)

    // 2. Initialize Single Instance logic
    val controller = remember {
        SingleInstanceManager(port = 9999) {
            // This runs in Instance #1 when Instance #2 is launched
            focusTrigger = System.currentTimeMillis()
        }
    }

    // 3. Exit if this is the second instance
    if (!controller.isFirstInstance()) {
        exitApplication()
        return@application
    }

    if (!isWindowsAdmin()) {
        elevateProcess() // We need this to clean our interfaces on cleanup
        return@application // Exit the non-admin process
    }

    initializeCoil()

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

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) dark else light
    ) {
        Window(
            onCloseRequest = {
                // 1. Get the VPNLauncher from Koin
                val launcher = GlobalContext.get().get<VPNLauncherInterface>()

                // 2. Perform cleanup before exiting
                launcher.stopVPN()

                // 3. Close the app
                exitApplication()
            },
            state = windowState,
            title = "KMP VPN",
            undecorated = true,
            resizable = false
        ) {
            LaunchedEffect(focusTrigger) {
                if (focusTrigger > 0) {
                    windowState.isMinimized = false
                    window.isAlwaysOnTop = true
                    window.toFront()
                    window.requestFocus()
                    window.isAlwaysOnTop = false
                }
            }

            Column {
                WindowDraggableArea {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = "KMP VPN",
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                            IconButton(onClick = { windowState.isMinimized = true }) {
                                Icon(imageVector = MaterialIconsMinimize, contentDescription = null)
                            }
                            IconButton(onClick = ::exitApplication) {
                                Icon(imageVector = VscodeCodiconsClose, contentDescription = null)
                            }
                        }
                    }
                }

                App(root = model)
            }
        }
    }
}