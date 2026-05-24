package io.iskopasi.kmpvpntest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.kmpvpntest.decompose.MainComponent
import org.jetbrains.compose.resources.painterResource

import kmpvpntest.shared.generated.resources.Res
import kmpvpntest.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App(model: MainComponent) {
    MaterialTheme {
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
        }
    }
}