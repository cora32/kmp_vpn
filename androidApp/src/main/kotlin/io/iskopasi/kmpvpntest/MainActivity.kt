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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.iskopasi.kmpvpntest.api.initializeCoil
import io.iskopasi.kmpvpntest.managers.PermissionType
import io.iskopasi.kmpvpntest.viewmodels.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        initializeCoil(this)
        super.onCreate(savedInstanceState)

        setContent {
            val homeViewModel: HomeViewModel = koinViewModel<HomeViewModel>()

            ListenForPermissionRequests(homeViewModel = homeViewModel)

            App(homeViewModel = homeViewModel)
        }
    }


    @Composable
    fun ListenForPermissionRequests(homeViewModel: HomeViewModel) {
        // Notification permission launcher
        val postPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                homeViewModel.onPostPermissionGranted()
            } else {
                homeViewModel.onPostPermissionDenied()
            }
        }

        // VPN permission launcher
        val vpnPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                homeViewModel.onVPNPermissionGranted()
            } else {
                homeViewModel.onVPNPermissionDenied()
            }
        }

        LaunchedEffect(Unit) {
            homeViewModel.permissionApi.permissionRequester.requestFlow.collect { permission ->
                when (permission) {
                    PermissionType.Vpn -> {
                        val intent = VpnService.prepare(application)

                        if (intent != null) {
                            vpnPermissionLauncher.launch(intent)
                        } else {
                            homeViewModel.onPostPermissionGranted()
                        }
                    }

                    PermissionType.Notification -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        postPermissionLauncher.launch(POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }
}
