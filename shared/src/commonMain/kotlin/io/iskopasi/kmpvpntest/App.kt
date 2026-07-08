package io.iskopasi.kmpvpntest

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import io.iskopasi.dns_filter.ui.DnsFilterScreen
import io.iskopasi.kmpvpntest.api.showToast
import io.iskopasi.kmpvpntest.decompose.RootComponent
import io.iskopasi.kmpvpntest.theme.LucideListFilterPlus
import io.iskopasi.kmpvpntest.theme.TablerRouteAltLeft
import io.iskopasi.kmpvpntest.theme.TablerRouter
import io.iskopasi.kmpvpntest.theme.dark
import io.iskopasi.kmpvpntest.theme.light
import io.iskopasi.kmpvpntest.ui.MainScreen
import io.iskopasi.kmpvpntest.utils.theme.cGray
import io.iskopasi.kmpvpntest.utils.theme.cWhite
import io.iskopasi.kmpvpntest.utils.theme.silver
import io.iskopasi.splittunnel.ui.SplitTunnelScreen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Main : Screen

    @Serializable
    data object DnsFilter : Screen

    data object SplitTunnel : Screen
}


@OptIn(ExperimentalSerializationApi::class)
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<Screen>()
        }
    }
}

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundTransition")

    val backgroundColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF00E676), // Vibrant Green
        targetValue = Color(0xFF00E676),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 25000
                Color(0xFF00E676) at 0 // Green
                Color(0xFFFFB74D) at 5000 // Peach
                Color(0xFF2979FF) at 10000 // Blue
                Color(0xFFFF5252) at 15000 // Red
                Color(0xFFFF6E40) at 20000 // Sunset
                Color(0xFF00E676) at 25000 // Back to Green
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ColorCycle"
    )

    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LineProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineHeight = 0.5.dp.toPx()
            val spacing = 10.dp.toPx()
            val lineCount = (size.height / spacing).toInt() + 2

            for (i in 0..lineCount) {
                val y = (i * spacing) + (lineProgress * spacing)
                drawRect(
                    color = Color.White.copy(alpha = 0.15f),
                    topLeft = Offset(0f, y),
                    size = Size(size.width, lineHeight)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(root: RootComponent) {
    LaunchedEffect(Unit) {
        root.eventBus.events.collect { event ->
            if (event.isNotEmpty()) {
                showToast(event)
            }
        }
    }

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) dark else light
    ) {
        val backStack = rememberNavBackStack(configuration = navConfig, Screen.Main)
        val isMainSelected by remember(backStack.last()) { mutableStateOf(backStack.last() == Screen.Main) }
        val isFilterSelected by remember(backStack.last()) { mutableStateOf(backStack.last() == Screen.DnsFilter) }
        val isSplitSelected by remember(backStack.last()) { mutableStateOf(backStack.last() == Screen.SplitTunnel) }

        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedBackground()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.Transparent,
                        ) {
                            Spacer(Modifier.weight(1f))
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 8.dp).width(270.dp).height(55.dp)
                                    .border(
                                        width = 0.4.dp,
                                        color = cWhite.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(45.dp)
                                    )
                                    .clip(RoundedCornerShape(45.dp))
                                    .background(silver)
                            ) {
                                NavItem(
                                    key = Screen.Main,
                                    isSelected = isMainSelected,
                                    text = "Main",
                                    backStack = backStack,
                                    imageVector = TablerRouter
                                )
                                NavItem(
                                    key = Screen.DnsFilter,
                                    isSelected = isFilterSelected,
                                    text = "Filtering",
                                    backStack = backStack,
                                    imageVector = LucideListFilterPlus
                                )
                                NavItem(
                                    key = Screen.SplitTunnel,
                                    isSelected = isSplitSelected,
                                    text = "Split Tunnel",
                                    backStack = backStack,
                                    imageVector = TablerRouteAltLeft
                                )
                            }
                            Spacer(Modifier.weight(1f))
                        }
                    }
                ) { padding ->
                    NavDisplay(backStack = backStack) { key ->
                        when (key) {
                            is Screen.Main -> NavEntry(key) {
                                MainScreen(
                                    component = root.main,
                                    padding = padding
                                )
                            }

                            is Screen.DnsFilter -> NavEntry(key) {
                                DnsFilterScreen(
                                    component = root.dnsFilterComponent,
                                    padding = padding
                                )
                            }

                            is Screen.SplitTunnel -> NavEntry(key) {
                                SplitTunnelScreen(
                                    component = root.splitTunnel,
                                    padding = padding
                                )
                            }

                            else -> error("Unknown screen: $key")
                        }
                    }
                }
            }
        }
    }
}

@Composable

fun RowScope.NavItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    key: Screen,
    text: String,
    imageVector: ImageVector,
    backStack: NavBackStack<NavKey>
) {
    NavigationBarItem(
        modifier = Modifier
            .fillMaxHeight()
            .background(if (isSelected) Color.White else silver),
        selected = isSelected,
        onClick = {
            if (backStack.last() != key) {
                backStack.clear()
                backStack.add(key)
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Black,
            selectedTextColor = Color.Black,
            indicatorColor = Color.Transparent,
            unselectedIconColor = cGray,
            unselectedTextColor = cGray,
            disabledIconColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.primary,
        ),
        icon = { Icon(imageVector, contentDescription = null) },
        label = { Text(text) }
    )
}