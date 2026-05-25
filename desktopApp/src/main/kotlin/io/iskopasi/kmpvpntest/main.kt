package io.iskopasi.kmpvpntest

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.iskopasi.kmpvpntest.decompose.RootComponent
import io.iskopasi.kmpvpntest.di.getModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(
            getModel(
                vpnPlatformApi = getVpnPlatformApi(),
            )
        )
    }

    val model = RootComponent(
        componentContext = DefaultComponentContext(
            LifecycleRegistry()
        ),
        koin = GlobalContext.get(),
        permissionApi = getPermissionApi()
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "kmpvpntest",
    ) {
        App(model = model.main)
    }
}