package io.iskopasi.kmpvpntest.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.iskopasi.kmpvpntest.decompose.MainComponent
import io.iskopasi.kmpvpntest.generated.resources.Res
import io.iskopasi.kmpvpntest.generated.resources.custom
import io.iskopasi.kmpvpntest.generated.resources.list
import io.iskopasi.kmpvpntest.utils.LogoText
import io.iskopasi.kmpvpntest.utils.theme.cGray
import io.iskopasi.kmpvpntest.utils.theme.silver
import org.jetbrains.compose.resources.stringResource

enum class ProxyUIState {
    List,
    Custom
}

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


            CompositeProxyBlock(component = component)
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
fun CompositeProxyBlock(modifier: Modifier = Modifier, component: MainComponent) {
    var uiState by remember { mutableStateOf(ProxyUIState.List) }

    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(0.dp, 450.dp).padding(16.dp),
        border = BorderStroke(width = 0.5.dp, color = Color.White.copy(alpha = 0.3f)),
        color = silver,
        contentColor = Color.Black,
        shape = RectangleShape,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        uiState = ProxyUIState.List

                        component.fetchNewProxies()
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (uiState == ProxyUIState.List) Color.White else Color.Transparent,
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(Res.string.list),
                        style = TextStyle(
                            color = cGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
                TextButton(
                    onClick = { uiState = ProxyUIState.Custom },
                    shape = RectangleShape,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (uiState == ProxyUIState.Custom) Color.White else Color.Transparent,
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(Res.string.custom),
                        style = TextStyle(
                            color = cGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            }
            AnimatedContent(uiState, contentAlignment = Alignment.Center) {
                when (it) {
                    ProxyUIState.List -> ListProxyBlock(component = component)
                    ProxyUIState.Custom -> CustomProxyBlock(component = component)
                }
            }
        }
    }
}
