package io.iskopasi.kmpvpntest

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.kmpvpntest.decompose.MainComponent
import io.iskopasi.kmpvpntest.theme.CloudMinimal
import io.iskopasi.kmpvpntest.theme.CloudPuffy
import io.iskopasi.kmpvpntest.theme.CloudSimple
import io.iskopasi.kmpvpntest.theme.CloudVolumetricWide
import io.iskopasi.kmpvpntest.theme.dark
import io.iskopasi.kmpvpntest.theme.light
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

@Composable
fun App(model: MainComponent) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) dark else light
    ) {
        val state by model.state.collectAsStateWithLifecycle()
        val isConnected = state == MainComponent.State.Connected

        // Moon Glow Animations
        val glowTransition = rememberInfiniteTransition()
        val pulseScale by glowTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val moonColor by animateColorAsState(
            targetValue = if (isConnected) Color(0xFFFFF9E3) else MaterialTheme.colorScheme.secondary.copy(
                alpha = 0.8f
            ),
            animationSpec = tween(1000)
        )

        val glowAlpha by animateFloatAsState(
            targetValue = if (isConnected) 0.4f else 0f,
            animationSpec = tween(1500)
        )

        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedBackground()

                Column(
                    modifier = Modifier
                        .safeContentPadding()
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .drawBehind {
                                if (glowAlpha > 0f) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = glowAlpha),
                                                Color.Transparent
                                            ),
                                            center = center,
                                            radius = size.width * pulseScale
                                        ),
                                        radius = size.width * pulseScale
                                    )
                                }
                            },
                        shape = CircleShape,
                        color = moonColor,
                        shadowElevation = if (isConnected) 24.dp else 8.dp,
                        tonalElevation = 4.dp
                    ) {
                        Button(
                            modifier = Modifier.fillMaxSize(),
                            onClick = model::onConnect,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = if (isConnected) Color.Black else Color.White
                            )
                        ) {
                            val stateText = when (state) {
                                MainComponent.State.Idle -> "Idle"
                                MainComponent.State.Connecting -> "Connecting"
                                MainComponent.State.Connected -> "Connected"
                                else -> "Idle"
                            }
                            Text(
                                stateText,
                                style = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ErrorBlock(model = model)

                    Spacer(modifier = Modifier.height(16.dp))

                    ProxyBlock(model = model)
                }
            }
        }
    }
}

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    // Star glimmer animation
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF000511), Color(0xFF0D1B2A)) // Deep night gradient
                )
            )
    ) {
        // Glimmering Stars Canvas
        Canvas(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            val random = Random(42) // Fixed seed so stars don't jump
            repeat(100) {
                drawCircle(
                    color = Color.White,
                    radius = random.nextFloat() * 1.5f + 0.5f,
                    center = Offset(
                        x = random.nextFloat() * size.width,
                        y = random.nextFloat() * size.height
                    ),
                    alpha = random.nextFloat() * starAlpha
                )
            }
        }

        // Night clouds
        Cloud(
            cloud = CloudVolumetricWide,
            transition = infiniteTransition,
            duration = 45000,
            height = 250.dp,
            width = 500.dp,
            yOffset = 30.dp,
            alpha = 0.1f
        )
        Cloud(
            cloud = CloudPuffy,
            transition = infiniteTransition,
            duration = 35000,
            height = 120.dp,
            width = 120.dp,
            yOffset = 60.dp,
            alpha = 0.15f
        )
        Cloud(
            cloud = CloudSimple,
            transition = infiniteTransition,
            duration = 25000,
            height = 130.dp,
            width = 100.dp,
            yOffset = 150.dp,
            alpha = 0.12f
        )
        Cloud(
            cloud = CloudMinimal,
            transition = infiniteTransition,
            duration = 20000,
            height = 100.dp,
            width = 800.dp,
            yOffset = 180.dp,
            alpha = 0.08f
        )
    }
}

@Composable
fun Cloud(
    cloud: ImageVector,
    transition: InfiniteTransition,
    duration: Int,
    height: androidx.compose.ui.unit.Dp,
    width: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    alpha: Float
) {
    // This animation goes from 1.5 (right side) to -0.5 (left side)
    // multiplying by the screen width in graphicsLayer
    val xOffset by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = -0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Icon(
        imageVector = cloud,
        contentDescription = null,
        tint = Color.White.copy(alpha = alpha),
        modifier = Modifier
            .height(height = height)
            .width(width = width)
            .offset(y = yOffset)
            .graphicsLayer {
                // 'translationX' moves the icon. 
                // We use a large enough range to ensure it starts and ends off-screen.
                translationX = xOffset * 500f
            }
    )
}

@Composable
fun ErrorBlock(
    modifier: Modifier = Modifier,
    model: MainComponent
) {
    val error by model.errorMessage.collectAsStateWithLifecycle()

    Text(
        error,
        style = TextStyle(
            color = Color.Red,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(32.dp)
    )
}

@Composable
@Preview(backgroundColor = 0xFFffffff)
fun ProxyBlock(modifier: Modifier = Modifier, model: MainComponent) {
    val isHostError by model.isHostError.collectAsStateWithLifecycle()
    val isPortError by model.isPortError.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    val isEnabled = state == MainComponent.State.Idle

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Input(
                label = "IP (socks5 server)",
                flow = model.host,
                isError = isHostError,
                onValueChange = model::onHostChanged,
                isEnabled = isEnabled,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                keyboardType = KeyboardType.Number
            )
            Input(
                label = "Port",
                flow = model.port,
                isError = isPortError,
                onValueChange = model::onPortChanged,
                isEnabled = isEnabled,
                modifier = Modifier.width(80.dp),
                keyboardType = KeyboardType.Number
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column {
            Input(
                label = "Username",
                flow = model.username,
                onValueChange = model::onUsernameChanged,
                isEnabled = isEnabled,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Input(
                label = "Password",
                flow = model.password,
                onValueChange = model::onPasswordChanged,
                modifier = Modifier,
                isEnabled = isEnabled,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            )
        }
    }
}

@Composable
fun Input(
    modifier: Modifier = Modifier,
    label: String,
    flow: StateFlow<String>,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val value by flow.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Text(
            label,
            style = TextStyle(
                fontSize = 12.sp,
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            enabled = isEnabled,
            value = value,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "") },
            onValueChange = onValueChange,
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction,
                keyboardType = keyboardType
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
    }
}
