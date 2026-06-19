package io.iskopasi.kmpvpntest.api

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent
import io.iskopasi.splittunnel.managers.AppManagerData

@ExperimentalMaterial3ExpressiveApi
@Composable
actual fun SplitTunnelScreen(component: SplitTunnelComponent) {
    val appList by component.appList.collectAsStateWithLifecycle()
    val showSystemApps by component.showSystemAppsFlow.collectAsStateWithLifecycle()
    val routeAllApps by component.routeAllAppsFlow.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement
                .SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text("Route all apps into the VPN")
            ToggleButton(
                checked = routeAllApps,
                onCheckedChange = component::toggleRouteAllApps
            ) {
                Text(if (routeAllApps) "Yes" else "No")
            }
        }
        Row(
            horizontalArrangement = Arrangement
                .SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text("Show system apps")
            ToggleButton(
                checked = showSystemApps,
                onCheckedChange = component::toggleShowSystemApps
            ) {

                Text(if (showSystemApps) "Yes" else "No")
            }
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(appList, key = { it.packageName }) { app ->
                AppItem(
                    modifier = Modifier.animateItem(),
                    app = app,
                    onAppChecked = component::onCheckApp,
                    enabled = !routeAllApps
                )
            }
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    app: AppManagerData,
    onAppChecked: (String, Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(app.name)
            Spacer(modifier = Modifier.height(4.dp))
            Text(app.packageName)
        }
        ToggleButton(
            enabled = enabled,
            checked = app.isChecked,
            onCheckedChange = { onAppChecked(app.packageName, it) }
        ) {
            Text(if (app.isChecked) "Enabled" else "Disabled")
        }
    }
}