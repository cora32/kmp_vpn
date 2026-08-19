package io.iskopasi.splittunnel.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SplitTunnelViewModelAndroid : SplitTunnelBaseViewModel(), KoinComponent {
    override fun onCheckApp(packageName: String, value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (value)
                prefStore.allowedApps += packageName
            else
                prefStore.allowedApps -= packageName

            onAppListChanged(prefStore.allowedApps)

            resortAppList(showSystemApp = _showSystemAppsFlow.value)
        }
    }
}

actual fun getSplitTunnelViewModel(): ISplitTunnelViewModel =
    SplitTunnelViewModelAndroid()