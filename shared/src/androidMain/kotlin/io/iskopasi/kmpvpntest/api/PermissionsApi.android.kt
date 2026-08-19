package io.iskopasi.kmpvpntest.api

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import androidx.core.content.ContextCompat
import io.iskopasi.kmpvpntest.managers.PermissionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPermissionApi : PermissionsApiDefault(), KoinComponent {
    private val application: Application by inject()
    private val _isVPNGrantedFlow = MutableStateFlow<Boolean>(false)
    private val _isNotificationGrantedFlow = MutableStateFlow<Boolean>(false)

    override val isVPNGrantedFlow = _isVPNGrantedFlow.asStateFlow()
    override val isNotificationGrantedFlow = _isNotificationGrantedFlow.asStateFlow()

    init {
        checkPostPermission()
        checkVPNPermission()
    }

    private fun checkPostPermission() {
        val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        setNotificationPermissionState(isGranted = isGranted)
    }

    private fun checkVPNPermission() {
        val intent = VpnService.prepare(application)
        val isGranted = intent == null

        setVPNPermissionState(isGranted = isGranted)
    }

    override fun setVPNPermissionState(isGranted: Boolean) {
        super.setVPNPermissionState(isGranted)

        _isVPNGrantedFlow.update {
            isGranted
        }
    }

    override fun setNotificationPermissionState(isGranted: Boolean) {
        super.setNotificationPermissionState(isGranted)

        _isNotificationGrantedFlow.update {
            isGranted
        }
    }

    override fun requestPermissions(): Pair<Boolean, Boolean> {
        permissionRequester.request(PermissionType.Notification)
        permissionRequester.request(PermissionType.Vpn)

        return isVPNGrantedFlow.value to isNotificationGrantedFlow.value
    }
}

actual fun getPermissionApi(): PermissionsApi = AndroidPermissionApi()