package io.iskopasi.kmpvpntest.managers

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class SingleInstanceManager(
    private val port: Int = 9999,
    private val onSecondInstance: () -> Unit
) {
    private var serverSocket: ServerSocket? = null

    /**
     * Attempts to claim the port.
     * @return true if this is the first instance, false otherwise.
     */
    fun isFirstInstance(): Boolean {
        return try {
            // Try to bind to the port on localhost.
            // This acts as a global mutex for the OS.
            serverSocket = ServerSocket(port, 1, InetAddress.getLoopbackAddress())

            // Start a daemon thread to listen for signals from second instances
            thread(isDaemon = true) {
                while (true) {
                    try {
                        serverSocket?.accept()?.use {
                            // When a second instance connects, trigger the callback
                            onSecondInstance()
                        }
                    } catch (e: IOException) {
                        break // Socket closed or app shutting down
                    }
                }
            }
            true
        } catch (e: IOException) {
            // Port is already taken, so another instance is already running.
            notifyFirstInstance()
            false
        }
    }

    private fun notifyFirstInstance() {
        try {
            // "Knock" on the port to tell the first instance to show its window
            Socket(InetAddress.getLoopbackAddress(), port).use { }
        } catch (e: Exception) {
            // First instance might have just been closed
        }
    }
}