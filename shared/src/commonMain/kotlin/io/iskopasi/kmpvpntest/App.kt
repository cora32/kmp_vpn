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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
                    Text(if (state.isConnected) "Disconnect" else "Connect")
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
        verticalArrangement = Arrangement.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Input(
                label = "Host",
                flow = model.host,
                onValueChange = model::onHostChanged,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Input(
                label = "Port",
                flow = model.port,
                onValueChange = model::onPortChanged,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Input(
                label = "Username",
                flow = model.username,
                onValueChange = model::onUsernameChanged,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Input(
                label = "Password",
                flow = model.password,
                onValueChange = model::onPasswordChanged,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
    }
}

@Composable
fun Input(
    modifier: Modifier = Modifier,
    label: String,
    flow: StateFlow<String>,
    onValueChange: (String) -> Unit
) {
    val value by flow.collectAsStateWithLifecycle()
    var state by remember { mutableStateOf(value) }

    Column {
        Text(
            label,
            style = TextStyle(
                fontSize = 12.sp,
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state,
            modifier = modifier,
            placeholder = { Text(text = "") },
            onValueChange = onValueChange
        )
    }
}