package io.iskopasi.kmpvpntest

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.kmpvpntest.decompose.MainComponent
import kotlinx.coroutines.flow.StateFlow

val lightColorScheme = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7d5260)
)

val darkColorScheme = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7d5260)
)

@Composable
fun App(model: MainComponent) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme else lightColorScheme
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val state by model.state.collectAsStateWithLifecycle()

            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                shadowElevation = 8.dp,
                tonalElevation = 4.dp
            ) {
                Button(
                    modifier = Modifier,
                    onClick = model::onConnect
                ) {
                    val stateText = when (state) {
                        MainComponent.State.Idle -> "Idle"
                        MainComponent.State.Connecting -> "Connecting"
                        MainComponent.State.Connected -> "Connected"
                    }
                    Text(stateText)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProxyBlock(model = model)
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFffffff)
fun ProxyBlock(modifier: Modifier = Modifier, model: MainComponent) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Input(
                label = "socks5://host",
                flow = model.host,
                onValueChange = model::onHostChanged,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Input(
                label = "Port",
                flow = model.port,
                onValueChange = model::onPortChanged,
                modifier = Modifier.width(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column {
            Input(
                label = "Username",
                flow = model.username,
                onValueChange = model::onUsernameChanged,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Input(
                label = "Password",
                flow = model.password,
                onValueChange = model::onPasswordChanged,
                modifier = Modifier,
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
    onValueChange: (String) -> Unit,
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
            value = value,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "") },
            onValueChange = onValueChange,
            singleLine = true,
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