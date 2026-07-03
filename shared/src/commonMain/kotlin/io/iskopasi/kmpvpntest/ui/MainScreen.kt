package io.iskopasi.kmpvpntest.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.kmpvpntest.decompose.MainComponent
import io.iskopasi.kmpvpntest.utils.theme.cGray
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MainScreen(modifier: Modifier = Modifier, component: MainComponent, padding: PaddingValues) {
    val state by component.state.collectAsStateWithLifecycle()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                    onClick = component::onConnect,
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
                        style = TextStyle(
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

//            Spacer(modifier = Modifier.height(16.dp))
//
//            ErrorBlock(component = component)

            Spacer(modifier = Modifier.height(16.dp))

            ProxyBlock(component = component)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun ErrorBlock(
    modifier: Modifier = Modifier,
    component: MainComponent
) {
    val error by component.errorMessage.collectAsStateWithLifecycle()

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
fun ProxyBlock(modifier: Modifier = Modifier, component: MainComponent) {
    val isHostError by component.isHostError.collectAsStateWithLifecycle()
    val isPortError by component.isPortError.collectAsStateWithLifecycle()
    val state by component.state.collectAsStateWithLifecycle()

    val isEnabled = state == MainComponent.State.Idle

    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        border = BorderStroke(width = 0.5.dp, color = Color.White.copy(alpha = 0.3f)),
        color = Color(0xFFD7D7D7).copy(alpha = 0.9f),
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Input(
                    label = "IP (socks5)",
                    initialValue = component.host,
                    isError = isHostError,
                    onValueChange = component::onHostChanged,
                    isEnabled = isEnabled,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    keyboardType = KeyboardType.Number
                )
                Input(
                    label = "Port",
                    initialValue = component.port,
                    isError = isPortError,
                    onValueChange = component::onPortChanged,
                    isEnabled = isEnabled,
                    modifier = Modifier.width(80.dp),
                    keyboardType = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Column {
                Input(
                    label = "Username",
                    initialValue = component.username,
                    onValueChange = component::onUsernameChanged,
                    isEnabled = isEnabled,
                    modifier = Modifier,
                    placeholder = "Leave empty if no auth required"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Input(
                    label = "Password",
                    initialValue = component.password,
                    onValueChange = component::onPasswordChanged,
                    modifier = Modifier,
                    isEnabled = isEnabled,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                    placeholder = "Leave empty if no auth required"
                )
            }
        }
    }
}

@Composable
fun Input(
    modifier: Modifier = Modifier,
    label: String,
    initialValue: String,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(initialValue))
    }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isFocused) {
        if (isFocused) {
            kotlinx.coroutines.delay(10.milliseconds)
            textFieldValue = textFieldValue.copy(
                selection = TextRange(0, textFieldValue.text.length)
            )
        }
    }

    Column(modifier = modifier) {
        Text(
            label,
            style = TextStyle(
                fontSize = 12.sp,
                color = when {
                    !isEnabled -> cGray.copy(alpha = 0.4f)
                    isFocused -> Color.Unspecified
                    else -> cGray.copy(alpha = 0.6f)
                }
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            enabled = isEnabled,
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onValueChange(it.text)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedTextColor = cGray,
                unfocusedTextColor = cGray.copy(alpha = 0.7f),
                disabledTextColor = cGray.copy(alpha = 0.4f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedLabelColor = Color.Unspecified,
                unfocusedLabelColor = cGray.copy(alpha = 0.7f),
                disabledLabelColor = cGray.copy(alpha = 0.4f),
                cursorColor = Color.Black,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.isFocused
                }
                .drawBehind {
                    if (isFocused) {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                },
            placeholder = {
                Text(
                    text = placeholder, style = TextStyle(
                        color = cGray.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
            },
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
