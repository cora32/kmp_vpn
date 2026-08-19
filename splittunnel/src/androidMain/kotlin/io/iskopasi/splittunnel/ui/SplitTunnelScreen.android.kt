@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.iskopasi.splittunnel.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.iskopasi.kmpvpntest.utils.theme.cWhite
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent
import io.iskopasi.splittunnel.managers.AppManagerData

@ExperimentalMaterial3ExpressiveApi
@Composable
actual fun SplitTunnelScreen(component: SplitTunnelComponent, padding: PaddingValues) {
    val toggleColors = ToggleButtonDefaults.toggleButtonColors(
        containerColor = Color.Transparent,
        contentColor = cWhite,
        disabledContentColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = cWhite,
        checkedContainerColor = cWhite,
        checkedContentColor = Color.Black
    )

    val appList by component.appList.collectAsStateWithLifecycle()
    val showSystemApps by component.showSystemAppsFlow.collectAsStateWithLifecycle()
    val routeAllApps by component.routeAllAppsFlow.collectAsStateWithLifecycle()
    val isLoading by component.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        component.getAppList()
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())
            .padding(top = 32.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement
                    .SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    "Route all apps into the VPN",
                    style = TextStyle(
                        color = cWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                ToggleButton(
                    checked = routeAllApps,
                    onCheckedChange = component::toggleRouteAllApps,
                    colors = toggleColors
                ) {
                    Text(if (routeAllApps) "Yes" else "No")
                }
            }
            Row(
                horizontalArrangement = Arrangement
                    .SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    "Show system apps",
                    style = TextStyle(
                        color = cWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                ToggleButton(
                    checked = showSystemApps,
                    onCheckedChange = component::toggleShowSystemApps,
                    colors = toggleColors
                ) {

                    Text(if (showSystemApps) "Yes" else "No")
                }
            }
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 170.dp)
            ) {
                items(appList, key = { it.packageName }) { app ->
                    AppItem(
                        app = app,
                        onAppChecked = component::onCheckApp,
                        enabled = !routeAllApps,
                        toggleColors = toggleColors,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = cWhite
            )
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    app: AppManagerData,
    onAppChecked: (String, Boolean) -> Unit,
    enabled: Boolean = true,
    toggleColors: ToggleButtonColors
) {
    val textColor = if (enabled) cWhite else MaterialTheme.colorScheme.tertiary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        AsyncImage(
            model = app.icon,
            contentDescription = "App Icon",
            modifier = Modifier.size(48.dp)
        )
        Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
            Text(
                app.name,
                style = TextStyle(
                    color = textColor,
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                app.packageName,
                style = TextStyle(
                    color = if (enabled) cWhite else MaterialTheme.colorScheme.tertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            )
        }
        ToggleButton(
            enabled = enabled,
            checked = app.isChecked,
            onCheckedChange = { onAppChecked(app.packageName, it) },
            colors = toggleColors
        ) {
            Text(if (app.isChecked || !enabled) "Enabled" else "Disabled")
        }
    }
}