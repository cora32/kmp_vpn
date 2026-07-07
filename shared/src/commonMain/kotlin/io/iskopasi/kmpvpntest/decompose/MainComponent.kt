package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.api.EventBus
import io.iskopasi.kmpvpntest.api.PermissionsApi
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.e
import io.iskopasi.kmpvpntest.managers.ProxyManager
import io.iskopasi.kmpvpntest.managers.SignalManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface MainComponent {
    val state: StateFlow<State>
    var isAuthEnabled: Boolean
    var isCertCheckEnabled: Boolean
    var host: String
    var port: String
    var username: String
    var password: String
    val errorMessage: StateFlow<String>
    val refreshSignalFlow: MutableStateFlow<Boolean>

    val isHostError: StateFlow<Boolean>
    val isPortError: StateFlow<Boolean>

    fun onConnect()
    fun onHostChanged(value: String)
    fun onPortChanged(value: String)
    fun onUsernameChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onAuthChanged(value: Boolean)
    fun onCertCheckChanged(value: Boolean)
    fun tryParseSocks5(text: String)

    enum class State {
        Idle,
        Connecting,
        Connected,
        Error
    }
}

class MainComponentImpl(
    private val componentContext: ComponentContext,
) : MainComponent, ComponentContext by componentContext, KoinComponent {
    private val prefStore: PrefStoreApi by inject()
    private val proxyManager: ProxyManager by inject()
    private val permissionApi: PermissionsApi by inject()
    private val signalManager: SignalManager by inject()
    private val eventBus: EventBus by inject()
    private val _state = MutableStateFlow(MainComponent.State.Idle)
    private val _isHostError = MutableStateFlow(false)
    private val _isPortError = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val state = _state.asStateFlow()
    override var isAuthEnabled
        get() = proxyManager.isAuthEnabled
        set(value) {
            proxyManager.isAuthEnabled = value
        }
    override var isCertCheckEnabled
        get() = prefStore.isCertCheckEnabled
        set(value) {
            prefStore.isCertCheckEnabled = value
        }
    override var host = proxyManager.proxyData.host
    override var port = proxyManager.proxyData.port
    override var username = proxyManager.proxyData.username
    override var password = proxyManager.proxyData.password
    override val errorMessage = _errorMessage.asStateFlow()
    override val isHostError = _isHostError.asStateFlow()
    override val isPortError = _isPortError.asStateFlow()
    override val refreshSignalFlow = MutableStateFlow(false)

    init {
        val errorJob = scope.launch {
            signalManager.errorBus
                .filter { it.isNotEmpty() }
                .collect { errorMsg ->
                    "errorMsg: $errorMsg".e
                    // Update UI state with error
                    _errorMessage.update { errorMsg }
                    setState(MainComponent.State.Idle)
                }
        }
    }

    private fun validateHost(host: String): Boolean {
        val isValid = host.isNotBlank()

        _isHostError.update {
            !isValid
        }

        return isValid
    }

    private fun validatePort(port: String): Boolean {
        val portInt = port.toIntOrNull()
        val isValid = portInt != null && portInt in 1..65535

        _isPortError.update {
            !isValid
        }

        return isValid
    }

    override fun onConnect() {
        val isHostValid = validateHost(host)
        val isPortValid = validatePort(port)

        if (!isHostValid || !isPortValid) return

        permissionApi.requestPermissions()

        clearErrors()

        scope.launch {
            combine(
                permissionApi.isVPNGrantedFlow,
                permissionApi.isNotificationGrantedFlow
            ) { isVPNGranted, isNotificationGranted ->
                isVPNGranted && isNotificationGranted
            }.collect { bothGranted ->
                if (bothGranted) {
                    // Decide whether to connect or disconnect based on CURRENT state
                    if (state.value == MainComponent.State.Connected) {
                        disconnect()
                    } else if (state.value == MainComponent.State.Idle) {
                        connect()
                    }
                }
            }
        }
    }

    override fun onAuthChanged(value: Boolean) {
        isAuthEnabled = value
    }

    override fun onCertCheckChanged(value: Boolean) {
        isCertCheckEnabled = value
    }

    override fun tryParseSocks5(text: String) {
        "Trying to parse: $text".e

        val trimmed = text.trim()
        // Regex patterns for the different formats
        val patterns = listOf(
            // socks5://username:password@host:port
            Regex("""^socks5://([^:@]+):([^:@]+)@([^:/]+):(\d+)$"""),
            // username:password@host:port
            Regex("""^([^:@]+):([^:@]+)@([^:/]+):(\d+)$"""),
            // socks5://host:port
            Regex("""^socks5://([^:/]+):(\d+)$"""),
            // host:port
            Regex("""^([^:/]+):(\d+)$""")
        )

        for ((index, regex) in patterns.withIndex()) {
            val match = regex.find(trimmed) ?: continue
            val groups = match.groupValues

            when (index) {
                0 -> { // socks5://username:password@host:port
                    onUsernameChanged(groups[1])
                    onPasswordChanged(groups[2])
                    onHostChanged(groups[3])
                    onPortChanged(groups[4])
                    onAuthChanged(true)
                }

                1 -> { // username:password@host:port
                    onUsernameChanged(groups[1])
                    onPasswordChanged(groups[2])
                    onHostChanged(groups[3])
                    onPortChanged(groups[4])
                    onAuthChanged(true)
                }

                2 -> { // socks5://host:port
                    onHostChanged(groups[1])
                    onPortChanged(groups[2])
                    onAuthChanged(false)
                }

                3 -> { // host:port
                    onHostChanged(groups[1])
                    onPortChanged(groups[2])
                    onAuthChanged(false)
                }
            }

            refreshSignalFlow.update {
                true
            }
            return // Stop after first successful match
        }

        // expected ip
        onHostChanged(text)
        refreshSignalFlow.update {
            true
        }
    }

    private fun clearErrors() {
        _errorMessage.update { "" }
    }

    private fun disconnect() {
        proxyManager.stopVPN()
        proxyManager.signalManager.reset()
        setState(MainComponent.State.Idle)
    }

    private fun connect() {
        setState(MainComponent.State.Connecting)

        scope.launch {
            signalManager.reset()

            try {
                val isOk = proxyManager.checkConnection(isCertCheckEnabled = isCertCheckEnabled)
                "[MainComponent] Proxy check: $isOk".e

                if (isOk) {
                    proxyManager.startVPN()

                    val isConnected = proxyManager.isConnected()
                    "[MainComponent] isConnected result: $isConnected".e

                    if (isConnected) {
                        setState(MainComponent.State.Connected)
                    } else {
                        setState(MainComponent.State.Idle)
                    }
                } else {
                    setState(MainComponent.State.Idle)
                    signalManager.onError("Connection error.\nCheck your proxy settings.")
                    eventBus.sendEvent("Connection error.\nCheck your proxy settings.")
                }
            } catch (ex: Exception) {
                setState(MainComponent.State.Idle)
                eventBus.sendEvent("${ex.message}")
            }
        }
    }

    private fun setState(state: MainComponent.State) {
        "[MainComponent] Setting state: $state".e

        _state.update {
            state
        }
    }

    override fun onHostChanged(value: String) {
        host = value

        proxyManager.setHost(value)
    }

    override fun onPortChanged(value: String) {
        port = value

        proxyManager.setPort(value)
    }

    override fun onUsernameChanged(value: String) {
        username = value

        proxyManager.setUsername(value)
    }

    override fun onPasswordChanged(value: String) {
        password = value

        proxyManager.setPassword(value)
    }

    fun onVPNPermissionGranted() = permissionApi.setVPNPermissionState(isGranted = true)
    fun onVPNPermissionDenied() = permissionApi.setVPNPermissionState(isGranted = false)

    fun onPostPermissionGranted() = permissionApi.setNotificationPermissionState(isGranted = true)
    fun onPostPermissionDenied() = permissionApi.setNotificationPermissionState(isGranted = false)
}
