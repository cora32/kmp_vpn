package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

const val SingboxExe = "sing-box.exe"
const val WintunDll = "wintun.dll"

class VPNLauncher : VPNLauncherInterface, KoinComponent {
    private val prefStore: PrefStoreApi by inject()
    private val signalManager: SignalManager by inject()
    private val workDir = File(System.getProperty("user.home"), ".kmpvpn").apply { mkdirs() }
    private var vpnProcess: Process? = null
    private var debugStreamHandler: Thread? = null

    private fun extractResource(resourceName: String): File {
        val file = File(workDir, resourceName)
        if (!file.exists()) {
            val stream = this.javaClass.classLoader.getResourceAsStream(resourceName)
            stream?.use { input ->
                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } ?: throw Exception("Resource $resourceName not found")
        }

        return file
    }

    override fun startVPN() {
        cleanup()

        Thread.sleep(1000)

        val proxyData = prefStore.proxyData

        // 1. Prepare Config
        val config = ConfigBuilder.getSocks5DesktopConfig(
            host = proxyData.host,
            port = proxyData.port,
            username = proxyData.username,
            password = proxyData.password,
            logLevel = "debug",
            routeAllAppsIntoVPN = prefStore.allowAllApps,
            allowedPackages = prefStore.allowedApps
        )
        val configFile = File(workDir, "config.json").apply { writeText(config) }

        // 2. Extract binaries from resources
        val exeFile = extractResource(resourceName = SingboxExe)
        extractResource(resourceName = WintunDll)

        // 3. Launch
        val pb = ProcessBuilder(
            "cmd", "/c",
            File(workDir, exeFile.name).absolutePath,
            "run", "-c", configFile.name
        )
        pb.directory(workDir)
        pb.redirectErrorStream(true)
        vpnProcess = pb.start()

        signalManager.onConnected()

        debugStreamHandler = Thread {
            try {
                vpnProcess?.inputStream?.bufferedReader()?.use { reader ->
                    while (!Thread.currentThread().isInterrupted) {
                        val line = reader.readLine() ?: break
                        println("[SINGBOX] $line")
                    }
                }
            } catch (e: Exception) {
                // Likely the stream was closed because the process was killed
                println("[VPNLauncher] Log reader stopped: ${e.message}")
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    private fun cleanup() {
        debugStreamHandler?.interrupt()
        debugStreamHandler = null

        try {
            ProcessBuilder(
                "taskkill", "/F", "/IM", "sing-box.exe", "/T"
            ).start().waitFor()

            ProcessBuilder(
                "netsh", "interface", "delete", "interface", "name=\"KMPVPN\""
            ).start().waitFor()
        } catch (ex: Exception) {
            println("[VPNLauncher] Cleanup error: ${ex.message}")
        }
    }

    override fun stopVPN() {
        cleanup()

        vpnProcess?.destroy()
        vpnProcess?.waitFor()
        vpnProcess = null
    }
}