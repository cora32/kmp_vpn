@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.iskopasi.splittunnel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import io.iskopasi.kmpvpntest.utils.theme.cWhite
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent
import io.iskopasi.splittunnel.managers.AppManagerData

@Composable
actual fun SplitTunnelScreen(
    component: SplitTunnelComponent,
    padding: PaddingValues
) {
    val runningProcesses by component.runningProcessesFlow.collectAsStateWithLifecycle()
    val allowedApps by component.allowedAppsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        component.getProcessList()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunningProcessesBox(
            runningProcesses = runningProcesses,
            onAddApp = component::onAddApp,
            getProcessList = component::getProcessList
        )

        Spacer(modifier = Modifier.height(16.dp))

        AllowedApps(allowedApps = allowedApps, onRemove = component::onRemoveApp)

        Button(
            modifier = Modifier.padding(bottom = 80.dp),
            onClick = component::onSelectFile,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = cWhite
            )
        ) {
            Text(
                "Select by file",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun ColumnScope.AllowedApps(
    modifier: Modifier = Modifier,
    allowedApps: List<AppManagerData>,
    onRemove: (AppManagerData) -> Unit
) {
    Box(
        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f)).padding(vertical = 16.dp)
            .weight(1f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Apps routed into the VPN",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (allowedApps.isEmpty())
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "If no app is selected - every app is selected.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 16.dp, end = 16.dp)
            ) {
                items(allowedApps, key = { it.packageName }) { processName ->
                    RunningProcessItemWithRemoveButton(
                        data = processName,
                        onRemove = onRemove,
                        modifier = Modifier.animateItem()
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ColumnScope.RunningProcessesBox(
    modifier: Modifier = Modifier,
    runningProcesses: List<AppManagerData>,
    onAddApp: (AppManagerData) -> Unit,
    getProcessList: () -> Unit
) {
    Box(
        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f)).padding(vertical = 16.dp)
            .weight(1f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Running processes",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 16.dp, end = 16.dp)
            ) {
                items(runningProcesses, key = { it.packageName }) { processName ->
                    RunningProcessItemName(
                        data = processName,
                        onTap = onAddApp,
                        modifier = Modifier.animateItem()
                    )
                    HorizontalDivider()
                }
            }
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = getProcessList,
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                contentColor = cWhite
            )
        ) {
            Icon(
                imageVector = VscodeCodiconsRefresh,
                contentDescription = "Refresh"
            )
        }
    }
}

@Composable
fun RunningProcessItemWithRemoveButton(
    modifier: Modifier = Modifier,
    data: AppManagerData,
    onRemove: (AppManagerData) -> Unit
) {
    Row(
        modifier = modifier then Modifier
            .background(if (data.isChecked) cWhite else Color.Transparent)
            .clickable(onClick = {
                onRemove(data)
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RunningProcessItemPath(data = data, onTap = {}, modifier = Modifier.weight(1f))
        TextButton(onClick = {
            onRemove(data)
        }) {
            Text(
                "Remove",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),

                )
        }
    }
}

@Composable
fun RunningProcessItemName(
    modifier: Modifier = Modifier,
    data: AppManagerData,
    onTap: (AppManagerData) -> Unit
) {
    Row(
        modifier = modifier then Modifier
            .fillMaxWidth()
            .background(if (data.isChecked) cWhite else Color.Transparent)
            .clickable(onClick = {
                onTap(data)
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.icon,
            imageLoader = SingletonImageLoader.get(LocalPlatformContext.current),
            contentDescription = null,
            modifier = Modifier.size(24.dp).padding(4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            data.name,
            style = TextStyle(
                color = if (data.isChecked) MaterialTheme.colorScheme.primary else cWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            data.packageName,
            style = TextStyle(
                color = if (data.isChecked) MaterialTheme.colorScheme.primary else cWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RunningProcessItemPath(
    modifier: Modifier = Modifier,
    data: AppManagerData,
    onTap: (AppManagerData) -> Unit
) {
    Row(
        modifier = modifier then Modifier
            .fillMaxWidth()
            .background(if (data.isChecked) cWhite else Color.Transparent)
            .clickable(onClick = {
                onTap(data)
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.icon,
            imageLoader = SingletonImageLoader.get(LocalPlatformContext.current),
            contentDescription = null,
            modifier = Modifier.size(24.dp).padding(4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            data.packageName,
            style = TextStyle(
                color = if (data.isChecked) MaterialTheme.colorScheme.primary else cWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )
    }
}