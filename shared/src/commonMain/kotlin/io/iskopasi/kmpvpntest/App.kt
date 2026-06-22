@file:OptIn(ExperimentalSerializationApi::class)

package io.iskopasi.kmpvpntest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import io.iskopasi.kmpvpntest.api.SplitTunnelScreen
import io.iskopasi.kmpvpntest.decompose.RootComponent
import io.iskopasi.kmpvpntest.theme.TablerRouteAltLeft
import io.iskopasi.kmpvpntest.theme.TablerRouter
import io.iskopasi.kmpvpntest.theme.cWhite
import io.iskopasi.kmpvpntest.theme.dark
import io.iskopasi.kmpvpntest.theme.light
import io.iskopasi.kmpvpntest.ui.MainScreen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@kotlinx.serialization.Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Main : Screen

    @kotlinx.serialization.Serializable
    data object SplitTunnel : Screen
}


private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<Screen>()
        }
    }
}

@Composable
fun App(root: RootComponent) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) dark else light
    ) {
        val backStack = rememberNavBackStack(configuration = navConfig, Screen.Main)
        val isMainSelected by remember(backStack.last()) { mutableStateOf(backStack.last() == Screen.Main) }
        val isSplitSelected by remember(backStack.last()) { mutableStateOf(backStack.last() == Screen.SplitTunnel) }

        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.Transparent,
                    ) {

                        Spacer(Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .padding(bottom = 8.dp).width(250.dp).height(60.dp)
                                .border(
                                    width = 0.4.dp,
                                    color = cWhite.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(45.dp)
                                )
                                .clip(RoundedCornerShape(45.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            NavigationBarItem(
                                modifier = Modifier.fillMaxHeight()
                                    .background(if (isMainSelected) Color.White else Color.Transparent),
                                selected = isMainSelected,
                                onClick = {
                                    if (backStack.last() != Screen.Main) {
                                        backStack.clear()
                                        backStack.add(Screen.Main)
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = cWhite,
                                    unselectedTextColor = cWhite,
                                    disabledIconColor = MaterialTheme.colorScheme.primary,
                                    disabledTextColor = MaterialTheme.colorScheme.primary,
                                ),
                                icon = { Icon(TablerRouter, contentDescription = null) },
                                label = { Text("VPN") }
                            )
                            NavigationBarItem(
                                modifier = Modifier.fillMaxHeight()
                                    .background(if (isSplitSelected) Color.White else Color.Transparent),
                                selected = isSplitSelected,
                                onClick = {
                                    if (backStack.last() != Screen.SplitTunnel) {
                                        backStack.clear()
                                        backStack.add(Screen.SplitTunnel)
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = cWhite,
                                    unselectedTextColor = cWhite,
                                    disabledIconColor = MaterialTheme.colorScheme.primary,
                                    disabledTextColor = MaterialTheme.colorScheme.primary,
                                ),
                                icon = { Icon(TablerRouteAltLeft, contentDescription = null) },
                                label = { Text("Split Tunnel") }
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }
            ) { padding ->
                NavDisplay(backStack = backStack) { key ->
                    when (key) {
                        is Screen.Main -> NavEntry(key) { MainScreen(component = root.main) }
                        is Screen.SplitTunnel -> NavEntry(key) { SplitTunnelScreen(root.splitTunnel) }
                        else -> error("Unknown screen: $key")
                    }
                }
            }
        }
    }
}