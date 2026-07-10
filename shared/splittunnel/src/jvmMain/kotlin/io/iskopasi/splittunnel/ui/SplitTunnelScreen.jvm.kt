@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.iskopasi.splittunnel.ui

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import io.iskopasi.kmpvpntest.utils.theme.cWhite
import io.iskopasi.splittunnel.decompose.SplitTunnelComponent
import io.iskopasi.splittunnel.generated.resources.Res
import io.iskopasi.splittunnel.generated.resources.loading_processes
import io.iskopasi.splittunnel.generated.resources.pick_executable
import io.iskopasi.splittunnel.generated.resources.refresh
import io.iskopasi.splittunnel.generated.resources.remove
import io.iskopasi.splittunnel.generated.resources.route_these_apps
import io.iskopasi.splittunnel.generated.resources.routing_everything
import io.iskopasi.splittunnel.generated.resources.running_processes
import io.iskopasi.splittunnel.managers.AppManagerData
import org.jetbrains.compose.resources.stringResource

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
        modifier = Modifier.fillMaxSize().padding(
            top = padding.calculateTopPadding() + 32.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = padding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunningProcessesBox(
            runningProcesses = runningProcesses,
            onAddApp = component::onAddApp,
            getProcessList = component::getProcessList,
            onSelectExe = component::onSelectFile,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AllowedApps(allowedApps = allowedApps, onRemove = component::onRemoveApp)
    }
}

@Composable
fun ColumnScope.AllowedApps(
    modifier: Modifier = Modifier,
    allowedApps: List<AppManagerData>,
    onRemove: (AppManagerData) -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ).padding(vertical = 16.dp)
            .weight(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.route_these_apps),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = allowedApps,
                contentAlignment = Alignment.Center
            ) {
                when {
                    it.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(Res.string.routing_everything),
                            style = TextStyle(
                                color = cWhite.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            ),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Light
                        )
                    }

                    else -> RoutedAppsList(
                        items = it,
                        onRemove = onRemove,
                    )
                }
            }
        }
    }
}

@Composable
fun RoutedAppsList(
    modifier: Modifier = Modifier,
    items: List<AppManagerData>,
    onRemove: (AppManagerData) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 16.dp, end = 16.dp)
    ) {
        items(items, key = { it.packageName }) { processName ->
            RunningProcessItemWithRemoveButton(
                data = processName,
                onRemove = onRemove,
                modifier = Modifier.animateItem()
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ColumnScope.RunningProcessesBox(
    modifier: Modifier = Modifier,
    runningProcesses: List<AppManagerData>,
    onAddApp: (AppManagerData) -> Unit,
    getProcessList: () -> Unit,
    onSelectExe: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(vertical = 16.dp)
            .weight(1f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(Res.string.running_processes),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            AnimatedContent(
                modifier = Modifier.fillMaxSize()
                    .weight(1f),
                targetState = runningProcesses,
                contentAlignment = Alignment.Center
            ) {
                when {
                    it.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(Res.string.loading_processes),
                            style = TextStyle(
                                color = cWhite.copy(alpha = 0.5f),
                                fontSize = 13.sp

                            ),
                            fontWeight = FontWeight.Light
                    )
                    }

                    else -> RunningProcList(
                        items = it,
                        onAddApp = onAddApp,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = onSelectExe,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    contentColor = cWhite
                )
            ) {
                Icon(
                    imageVector = VscodeCodiconsAdd,
                    contentDescription = stringResource(Res.string.pick_executable)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = getProcessList,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    contentColor = cWhite
                )
            ) {
                Icon(
                    imageVector = VscodeCodiconsRefresh,
                    contentDescription = stringResource(Res.string.refresh)
                )
            }
        }
    }
}

@Composable
fun RunningProcList(
    modifier: Modifier = Modifier,
    items: List<AppManagerData>,
    onAddApp: (AppManagerData) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 16.dp, end = 16.dp)
    ) {
        items(items, key = { it.packageName }) { processName ->
            RunningProcessItemName(
                data = processName,
                onTap = onAddApp,
                modifier = Modifier.animateItem()
            )
            HorizontalDivider(
                thickness = 0.5.dp,
                color = cWhite
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
                stringResource(Res.string.remove),
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
        Column(
            modifier = Modifier.fillMaxWidth().height(38.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                data.name,
                style = TextStyle(
                    color = if (data.isChecked) MaterialTheme.colorScheme.primary else cWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                data.packageName,
                style = TextStyle(
                    color = if (data.isChecked) MaterialTheme.colorScheme.primary else cWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                ),
                modifier = Modifier.weight(1f)
            )
        }
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