package io.iskopasi.kmpvpntest.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RuntimePermissionsDialog(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {
    if (ContextCompat.checkSelfPermission(
            LocalContext.current,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val permissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    onPermissionGranted()
                } else {
                    onPermissionDenied()
                }
            }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(permission)
        }
    }

}