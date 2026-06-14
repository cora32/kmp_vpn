package io.iskopasi.splittunnel.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.iskopasi.splittunnel.SplitTunnelComponent

@Composable
fun SplitTunnelUI(component: SplitTunnelComponent) {
    val model by component.model.subscribeAsState()
    var showProcessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    component.onRefreshProcesses()
                    showProcessDialog = true
                }
            ) {
                Text("Select from running processes")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = component::onSelectFile
            ) {
                Text("Select .exe")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Applications:", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(model.selectedApps) { appName ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        appName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = { component.onRemoveApp(appName) }
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }

    if (showProcessDialog) {
        Dialog(onDismissRequest = { showProcessDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxSize(0.8f),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Running Processes", style = MaterialTheme.typography.titleLarge)
                        Button(onClick = component::onRefreshProcesses) {
                            Text("Refresh")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        items(model.runningProcesses) { processName ->
                            Text(
                                text = processName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        component.onAddApp(processName)
                                        showProcessDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            HorizontalDivider()
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { showProcessDialog = false }
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
