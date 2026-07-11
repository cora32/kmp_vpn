package io.iskopasi.kmpvpntest.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.kmpvpntest.api.isAndroid
import io.iskopasi.kmpvpntest.decompose.MainComponent
import io.iskopasi.kmpvpntest.generated.resources.Res
import io.iskopasi.kmpvpntest.generated.resources.connect
import io.iskopasi.kmpvpntest.generated.resources.connecting
import io.iskopasi.kmpvpntest.generated.resources.disconnect
import io.iskopasi.kmpvpntest.utils.LogoText
import io.iskopasi.kmpvpntest.utils.theme.cGray
import io.iskopasi.kmpvpntest.utils.theme.cGray2
import io.iskopasi.kmpvpntest.utils.theme.silver
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(modifier: Modifier = Modifier, component: MainComponent, padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Logo()

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProxyBlock(component = component)

                Spacer(modifier = Modifier.height(16.dp))

                PowerButton(component = component)
            }
        }
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Text(
        LogoText, style = TextStyle(
            fontFamily = FontFamily.Monospace,
            color = cGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        )
    )
}

@Composable
fun PowerButton(modifier: Modifier = Modifier, component: MainComponent) {
    val state by component.state.collectAsStateWithLifecycle()

    val buttonText = when (state) {
        MainComponent.State.Idle -> stringResource(Res.string.connect)
        MainComponent.State.Connecting -> stringResource(Res.string.connecting)
        MainComponent.State.Connected -> stringResource(Res.string.disconnect)
        else -> "Off"
    }

    Surface(
        modifier = Modifier.width(140.dp).height(60.dp).padding(8.dp),
        border = BorderStroke(
            width = 0.5.dp,
            color = if (state == MainComponent.State.Connected) Color.White.copy(alpha = 0.7f) else Color.White.copy(
                alpha = 0.3f
            )
        ),
        color = Color.Transparent,
        contentColor = Color.Black,
        shape = CircleShape,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        onClick = component::onConnect
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                buttonText,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = if (state == MainComponent.State.Connected) Color.White.copy(alpha = 0.7f) else Color.White.copy(
                        alpha = 0.3f
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            )
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
    val refreshSignal by component.refreshSignalFlow.collectAsStateWithLifecycle()
    val isCertCheckEnabledState = remember { mutableStateOf(component.isCertCheckEnabled) }
    val isAuthEnabledState = remember { mutableStateOf(component.isAuthEnabled) }
    val isEnabled = state == MainComponent.State.Idle
    val hostState = remember(refreshSignal) {
        mutableStateOf(component.host)
    }
    val portState = remember(refreshSignal) { mutableStateOf(component.port) }
    val usernameState = remember(refreshSignal) { mutableStateOf(component.username) }
    val passwordState = remember(refreshSignal) { mutableStateOf(component.password) }

    LaunchedEffect(component.isAuthEnabled) {
        isAuthEnabledState.value = component.isAuthEnabled
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        border = BorderStroke(width = 0.5.dp, color = Color.White.copy(alpha = 0.3f)),
        color = silver,
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
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
                    state = hostState,
                    isError = isHostError,
                    onValueChange = component::onHostChanged,
                    isEnabled = isEnabled,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    keyboardType = KeyboardType.Number
                )
                Input(
                    label = "Port",
                    state = portState,
                    isError = isPortError,
                    onValueChange = component::onPortChanged,
                    isEnabled = isEnabled,
                    modifier = Modifier.width(80.dp),
                    keyboardType = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isAndroid)
                TextCheckbox(
                    modifier = Modifier.align(Alignment.End),
                    state = isCertCheckEnabledState,
                    text = "Enable certificate check:",
                    onCheckedChange = {
                        component.onCertCheckChanged(it)
                    }
                )
            Spacer(modifier = Modifier.height(8.dp))
            TextCheckbox(
                modifier = Modifier.align(Alignment.End),
                state = isAuthEnabledState,
                text = "Enable authorization:",
                onCheckedChange = {
                    component.onAuthChanged(it)
                }
            )
            AnimatedVisibility(
                visible = isAuthEnabledState.value,

                ) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Input(
                        label = "Username",
                        state = usernameState,
                        onValueChange = component::onUsernameChanged,
                        isEnabled = isEnabled && isAuthEnabledState.value,
                        modifier = Modifier,
                        placeholder = "Leave empty if no auth required"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Input(
                        label = "Password",
                        state = passwordState,
                        onValueChange = component::onPasswordChanged,
                        modifier = Modifier,
                        isEnabled = isEnabled && isAuthEnabledState.value,
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                        placeholder = "Leave empty if no auth required"
                    )
                }
            }
        }
    }
}

@Composable
fun TextCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    state: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            style = TextStyle(
                color = cGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
        )
        Checkbox(
            checked = state.value,
            onCheckedChange = {
                state.value = it
                onCheckedChange(it)
            },
            colors = CheckboxDefaults.colors().copy(
                checkedCheckmarkColor = Color.Black,
                uncheckedCheckmarkColor = Color.Black,
                checkedBoxColor = Color.Transparent,
                uncheckedBoxColor = Color.Transparent,
                checkedBorderColor = Color.Black,
                uncheckedBorderColor = Color.Black,
            )
        )
    }
}

@Composable
fun Input(
    modifier: Modifier = Modifier,
    label: String,
    state: MutableState<String>,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = "",
    onPaste: ((String) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
            value = state.value,
            onValueChange = {
                val isPasted = onPaste != null

                if (isPasted) {
                    onPaste.invoke(it)
                } else {
                    state.value = it
                    onValueChange(it)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = cGray2.copy(alpha = 0.15f),
                unfocusedContainerColor = cGray2.copy(alpha = 0.15f),
                disabledContainerColor = cGray2.copy(alpha = 0.15f),
                errorContainerColor = cGray2.copy(alpha = 0.15f),
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                errorTextColor = cGray,
                errorCursorColor = Color.Red,
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
