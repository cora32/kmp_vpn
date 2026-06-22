package io.iskopasi.kmpvpntest

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.defaultComponentContext
import io.iskopasi.kmpvpntest.api.PermissionsApi
import io.iskopasi.kmpvpntest.api.initializeCoil
import io.iskopasi.kmpvpntest.decompose.RootComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val permissionApi: PermissionsApi by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        initializeCoil(this)
        super.onCreate(savedInstanceState)

        val model = RootComponent(
            componentContext = defaultComponentContext(),
        )

        setContent {
            val vpnPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    model.main.onVPNPermissionGranted()
                } else {
                    model.main.onVPNPermissionDenied()
                }
            }
            val postPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    model.main.onPostPermissionGranted()
                } else {
                    model.main.onPostPermissionDenied()
                }
            }

            var requestVPNPermission by permissionApi.requestVPNPermission
            LaunchedEffect(requestVPNPermission) {
                if (requestVPNPermission) {
                    val intent = VpnService.prepare(application)

                    if (intent != null) {
                        vpnPermissionLauncher.launch(intent)
                    } else {
                        model.main.onPostPermissionGranted()
                    }
                    requestVPNPermission = false
                }
            }

            var requestPostPermission by permissionApi.requestPostPermission
            LaunchedEffect(requestPostPermission) {
                if (requestPostPermission) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        postPermissionLauncher.launch(POST_NOTIFICATIONS)
                        requestPostPermission = false
                    }
                }
            }

            App(root = model)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                RuntimePermissionsDialog(
//                    POST_NOTIFICATIONS,
//                    onPermissionGranted = {
//                    },
//                    onPermissionDenied = {
//                    },
//                )
//            }
        }
    }
}