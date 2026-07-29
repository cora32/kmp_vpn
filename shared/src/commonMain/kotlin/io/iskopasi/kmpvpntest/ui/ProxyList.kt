package io.iskopasi.kmpvpntest.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.kmpvpntest.decompose.MainComponent
import io.iskopasi.kmpvpntest.generated.resources.Res
import io.iskopasi.kmpvpntest.generated.resources.connect
import io.iskopasi.kmpvpntest.generated.resources.connecting
import io.iskopasi.kmpvpntest.generated.resources.disconnect
import io.iskopasi.kmpvpntest.generated.resources.disconnecting
import io.iskopasi.kmpvpntest.managers.ProxyData
import io.iskopasi.kmpvpntest.utils.theme.cDarkGray
import io.iskopasi.kmpvpntest.utils.theme.cGray
import io.iskopasi.kmpvpntest.utils.theme.cGray2
import io.iskopasi.kmpvpntest.utils.theme.cRed
import io.iskopasi.kmpvpntest.utils.theme.cWhite
import org.jetbrains.compose.resources.stringResource

private val ItemRowModifier = Modifier.fillMaxWidth()
private val ItemPaddingModifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
private val ItemButtonModifier = Modifier.padding(horizontal = 8.dp).width(90.dp).height(25.dp)

@Composable
fun ListProxyBlock(modifier: Modifier = Modifier, component: MainComponent) {
    val list by component.proxyList.collectAsStateWithLifecycle()
    val isProxyListLoading by component.isProxyListLoading.collectAsStateWithLifecycle()
    val state by component.state.collectAsStateWithLifecycle()

    Box(contentAlignment = Alignment.Center)
    {
        AnimatedContent(targetState = isProxyListLoading, contentAlignment = Alignment.Center) {
            when (it) {
                true ->
                    Box {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = cDarkGray
                        )
                    }

                false ->
                    LazyColumn {
                        items(count = list.size, key = { item -> item }) { index ->
                            val item = list[index]
                            val isSelected =
                                item.host == component.host && item.port == component.port
                            val isConnected = isSelected && state == MainComponent.State.Connected
                            val isConnecting = isSelected && state == MainComponent.State.Connecting
                            val isDisconnecting =
                                isSelected && state == MainComponent.State.Disconnecting
                            val bgColor =
                                if (index % 2 == 0) cWhite.copy(alpha = 0.8f) else cWhite.copy(alpha = 0.6f)

                            ProxyListItem(
                                item = item,
                                isSelected = isSelected,
                                isConnected = isConnected,
                                isConnecting = isConnecting,
                                isDisconnecting = isDisconnecting,
                                isEnabled = state != MainComponent.State.Connecting,
                                bgColor = bgColor,
                                onClick = component::onConnect
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun ProxyListItem(
    modifier: Modifier = Modifier,
    item: ProxyData,
    isSelected: Boolean,
    isConnected: Boolean,
    isConnecting: Boolean,
    isDisconnecting: Boolean,
    isEnabled: Boolean,
    bgColor: Color,
    onClick: (ProxyData) -> Unit,
) {
    val buttonText = when {
        isDisconnecting -> stringResource(Res.string.disconnecting)
        isConnecting -> stringResource(Res.string.connecting)
        isConnected -> stringResource(Res.string.disconnect)
        else -> stringResource(Res.string.connect)
    }
    val buttonTextColor = when {
        !isEnabled -> cGray.copy(alpha = 0.5f)
        isConnected -> cWhite
        else -> cGray2
    }
    val buttonBgColor = when {
        !isEnabled -> cGray2.copy(alpha = 0.5f)
        isConnected -> cRed
        else -> Color.Transparent
    }

    Row(
        ItemRowModifier
            .background(if (isSelected) Color.White else bgColor)
            .clickable(
                enabled = isEnabled,
                onClick = {
                    onClick(item)
                }
            )
            .then(ItemPaddingModifier),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.host}:${item.port}",
            style = TextStyle(
                color = cGray,
                fontSize = 14.sp
            ),
        )
        TextButton(
            enabled = isEnabled,
            onClick = {
                onClick(item)
            },
            shape = RectangleShape,
            colors = ButtonDefaults.textButtonColors(
                containerColor = buttonBgColor
            ),
            contentPadding = PaddingValues(vertical = 0.dp),
            modifier = ItemButtonModifier
        ) {
            Text(
                text = buttonText,
                style = TextStyle(
                    color = buttonTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }

}