package io.iskopasi.kmpvpntest.services

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.net.wifi.WifiManager
import android.os.Build
import android.os.ParcelFileDescriptor
import io.iskopasi.kmpvpntest.ClientIP
import io.iskopasi.kmpvpntest.DNSServer
import io.iskopasi.kmpvpntest.DefaultRoute
import io.iskopasi.kmpvpntest.HostExtra
import io.iskopasi.kmpvpntest.LogLevelExtra
import io.iskopasi.kmpvpntest.MainActivity
import io.iskopasi.kmpvpntest.PasswordExtra
import io.iskopasi.kmpvpntest.PortExtra
import io.iskopasi.kmpvpntest.SessionName
import io.iskopasi.kmpvpntest.StartCommand
import io.iskopasi.kmpvpntest.StopCommand
import io.iskopasi.kmpvpntest.UsernameExtra
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.e
import io.iskopasi.kmpvpntest.managers.NManager
import io.iskopasi.kmpvpntest.managers.SignalManager
import io.iskopasi.kmpvpntest.managers.getConfigBuilder
import io.nekohasekai.libbox.CommandServer
import io.nekohasekai.libbox.CommandServerHandler
import io.nekohasekai.libbox.ConnectionOwner
import io.nekohasekai.libbox.InterfaceUpdateListener
import io.nekohasekai.libbox.Libbox
import io.nekohasekai.libbox.LocalDNSTransport
import io.nekohasekai.libbox.NeighborUpdateListener
import io.nekohasekai.libbox.NetworkInterface
import io.nekohasekai.libbox.NetworkInterfaceIterator
import io.nekohasekai.libbox.Notification
import io.nekohasekai.libbox.OverrideOptions
import io.nekohasekai.libbox.PlatformInterface
import io.nekohasekai.libbox.SetupOptions
import io.nekohasekai.libbox.StringIterator
import io.nekohasekai.libbox.SystemProxyStatus
import io.nekohasekai.libbox.TunOptions
import io.nekohasekai.libbox.WIFIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File

class VPNServiceImpl : VpnService(),
    CommandServerHandler,
    PlatformInterface {
    private val prefStoreApi: PrefStoreApi by inject()
    private val signalManager: SignalManager by inject()
    private val nManager: NManager by inject()
    private val dao: io.iskopasi.kmpvpntest.managers.FilterDao by inject()

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var commandServer: CommandServer? = null

    override fun onCreate() {
        super.onCreate()

        nManager.createChannel()

        nManager.startForeground(service = this)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        intent?.let { intent ->
            val action = intent.action

            when (action) {
                StartCommand -> {
                    val host = intent.getStringExtra(HostExtra)
                    val port = intent.getStringExtra(PortExtra)
                    val username = intent.getStringExtra(UsernameExtra)
                    val password = intent.getStringExtra(PasswordExtra)
                    val logLevel = intent.getStringExtra(LogLevelExtra)

                    onStartVpn(
                        host = host,
                        port = port,
                        username = username,
                        password = password,
                        logLevel = logLevel
                    )
                }

                StopCommand -> stopVpn()
            }
        }

        return START_STICKY
    }

    private fun onStartVpn(
        host: String?,
        port: String?,
        username: String?,
        password: String?,
        logLevel: String?
    ) {
        if (host == null || port == null) return

        try {
            vpnInterface?.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        serviceScope.launch {
            try {
                val filterList = dao.getDomains().map { it.domain }.toSet()

                val configData = getConfigBuilder().getConfig(
                    host = host,
                    port = port,
                    username = username,
                    password = password,
                    logLevel = logLevel,
                    allowedPackages = prefStoreApi.allowedAppsNamesOnly, // This is never used by Singbox for Android target as it is configured inside VpnService
                    routeAllAppsIntoVPN = prefStoreApi.routeAllApps,
                    filterList = filterList
                ).e

                // Configure control server
                setupSingbox()

                // Start singbox control server
                startCS()

                "Sending start VPN command...".e
                val overrideOptions = OverrideOptions()
                commandServer?.startOrReloadService(configData, overrideOptions)
            } catch (ex: Exception) {
                "doJob ex: $ex".e
                ex.printStackTrace()
                stopVpn()
            }
        }
    }

    private fun startCS() {
        commandServer = Libbox.newCommandServer(this, this)
        commandServer?.start()
    }

    private fun setupSingbox() {
        val workingDir = File(filesDir, "libbox")
        workingDir.mkdirs()

        val options = SetupOptions().apply {
            workingPath = workingDir.absolutePath
            basePath = workingDir.absolutePath
            tempPath = workingDir.absolutePath
            debug = true
        }

        Libbox.setup(options)
    }

    override fun onRevoke() {
        stopVpn()

        super.onRevoke()
    }

    private fun stopVpn() {
        try {
            // Stop engine
            commandServer?.closeService()
            commandServer?.close()
            commandServer = null
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            // Close android TUN
            vpnInterface?.close()
            vpnInterface = null

            stopForeground(STOP_FOREGROUND_REMOVE)
            nManager.cancel()

            signalManager.onDisconnected()

            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        nManager.cancel()
        signalManager.onDisconnected()

        super.onDestroy()
    }

    // PlatformHandler

    override fun autoDetectInterfaceControl(fd: Int) {
        protect(fd)
    }

    override fun clearDNSCache() {
    }

    override fun closeDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {
    }

    override fun closeNeighborMonitor(listener: NeighborUpdateListener?) {
    }

    override fun findConnectionOwner(
        ipProtocol: Int,
        sourceAddress: String?,
        sourcePort: Int,
        destinationAddress: String?,
        destinationPort: Int
    ): ConnectionOwner? {
        return ConnectionOwner()
    }

    override fun getInterfaces(): NetworkInterfaceIterator? {
        return object : NetworkInterfaceIterator {
            override fun hasNext(): Boolean = false
            override fun next(): NetworkInterface? = null
        }
    }

    override fun includeAllNetworks(): Boolean {
        return false
    }

    override fun localDNSTransport(): LocalDNSTransport? {
        return null
    }

    private fun getBuilder(): Builder {
        val builder = Builder()
        builder.addAddress(ClientIP, 24)
        builder.addDnsServer(DNSServer)
        builder.setSession(SessionName)
        builder.setMtu(1500)
        builder.addRoute(DefaultRoute, 0)

        val allowedApps = prefStoreApi.allowedAppsNamesOnly
        val routeAllApps = prefStoreApi.routeAllApps
        if (routeAllApps) {
            builder.addDisallowedApplication(packageName)
        } else if (allowedApps.isNotEmpty()) {
            allowedApps.forEach { packageName ->
                try {
                    builder.addAllowedApplication(packageName)
                } catch (e: Exception) {
                    // App might have been uninstalled
                }
            }
        } else {
            builder.addDisallowedApplication(packageName)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val configureIntent = Intent(this, MainActivity::class.java)
        builder.setConfigureIntent(
            PendingIntent.getActivity(
                this,
                0,
                configureIntent,
                pendingIntentFlags
            )
        )

        return builder
    }

    override fun openTun(options: TunOptions?): Int {
        if (options == null) return -1

        try {
            val builder = getBuilder()

            vpnInterface = builder.establish()

            "[Service] VPN started...".e
            val fd = vpnInterface?.fd ?: -1

            if (fd != -1)
                signalManager.onConnected()
            else
                signalManager.onDisconnected()

            return fd
        } catch (ex: Exception) {
            "openTun ex: $ex".e
            ex.printStackTrace()
        }

        return -1
    }

    override fun readWIFIState(): WIFIState? {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as? WifiManager
        val info = wifiManager?.connectionInfo

        if (info != null) {
            return Libbox.newWIFIState(info.ssid ?: "", info.bssid ?: "")
        }

        return null
    }

    override fun registerMyInterface(name: String?) {
    }

    override fun sendNotification(notification: Notification?) {
    }

    override fun startDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {
    }

    override fun startNeighborMonitor(listener: NeighborUpdateListener?) {
    }

    override fun systemCertificates(): StringIterator? {
        return null
    }

    override fun underNetworkExtension(): Boolean {
        return false
    }

    override fun usePlatformAutoDetectInterfaceControl(): Boolean {
        return true
    }

    override fun useProcFS(): Boolean {
        return false
    }

    // Command Server

    override fun getSystemProxyStatus(): SystemProxyStatus? {
        return null
    }

    override fun serviceReload() {
    }

    override fun serviceStop() {
        stopVpn()
    }

    override fun setSystemProxyEnabled(enabled: Boolean) {
    }

    override fun writeDebugMessage(message: String?) {
        if (message == null) return

        "[SingBox] $message".e
    }
}
