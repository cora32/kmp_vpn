package io.iskopasi.kmpvpntest.services

import android.net.VpnService
import io.nekohasekai.libbox.CommandServerHandler
import io.nekohasekai.libbox.ConnectionOwner
import io.nekohasekai.libbox.InterfaceUpdateListener
import io.nekohasekai.libbox.LocalDNSTransport
import io.nekohasekai.libbox.NeighborUpdateListener
import io.nekohasekai.libbox.NetworkInterfaceIterator
import io.nekohasekai.libbox.Notification
import io.nekohasekai.libbox.PlatformInterface
import io.nekohasekai.libbox.StringIterator
import io.nekohasekai.libbox.SystemProxyStatus
import io.nekohasekai.libbox.TunOptions
import io.nekohasekai.libbox.WIFIState

const val INTENT_PROXY_ID = "proxyId"
const val INTENT_OLD_PROXY_ID = "oldProxyId"
const val INTENT_HOST = "INTENT_HOST"
const val INTENT_PORT = "INTENT_PORT"
const val INTENT_USERNAME = "INTENT_USERNAME"
const val INTENT_PASSWORD = "INTENT_PASSWORD"
const val INTENT_LOG_LEVEL = "INTENT_LOG_LEVEL"

class VPNServiceImpl : VpnService(), PlatformInterface, CommandServerHandler {
    override fun autoDetectInterfaceControl(fd: Int) {
        TODO("Not yet implemented")
    }

    override fun clearDNSCache() {
        TODO("Not yet implemented")
    }

    override fun closeDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {
        TODO("Not yet implemented")
    }

    override fun closeNeighborMonitor(listener: NeighborUpdateListener?) {
        TODO("Not yet implemented")
    }

    override fun findConnectionOwner(
        ipProtocol: Int,
        sourceAddress: String?,
        sourcePort: Int,
        destinationAddress: String?,
        destinationPort: Int
    ): ConnectionOwner? {
        TODO("Not yet implemented")
    }

    override fun getInterfaces(): NetworkInterfaceIterator? {
        TODO("Not yet implemented")
    }

    override fun includeAllNetworks(): Boolean {
        TODO("Not yet implemented")
    }

    override fun localDNSTransport(): LocalDNSTransport? {
        TODO("Not yet implemented")
    }

    override fun openTun(options: TunOptions?): Int {
        TODO("Not yet implemented")
    }

    override fun readWIFIState(): WIFIState? {
        TODO("Not yet implemented")
    }

    override fun registerMyInterface(name: String?) {
        TODO("Not yet implemented")
    }

    override fun sendNotification(notification: Notification?) {
        TODO("Not yet implemented")
    }

    override fun startDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {
        TODO("Not yet implemented")
    }

    override fun startNeighborMonitor(listener: NeighborUpdateListener?) {
        TODO("Not yet implemented")
    }

    override fun systemCertificates(): StringIterator? {
        TODO("Not yet implemented")
    }

    override fun underNetworkExtension(): Boolean {
        TODO("Not yet implemented")
    }

    override fun usePlatformAutoDetectInterfaceControl(): Boolean {
        TODO("Not yet implemented")
    }

    override fun useProcFS(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSystemProxyStatus(): SystemProxyStatus? {
        TODO("Not yet implemented")
    }

    override fun serviceReload() {
        TODO("Not yet implemented")
    }

    override fun serviceStop() {
        TODO("Not yet implemented")
    }

    override fun setSystemProxyEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun writeDebugMessage(message: String?) {
        TODO("Not yet implemented")
    }

}
