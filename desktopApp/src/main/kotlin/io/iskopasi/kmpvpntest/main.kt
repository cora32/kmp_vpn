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

fun main() = application {
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