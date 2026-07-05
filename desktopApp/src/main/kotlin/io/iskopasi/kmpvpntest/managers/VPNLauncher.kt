package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.PrefStoreApi
import io.iskopasi.kmpvpntest.api.e
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

    init {
        Runtime.getRuntime()
            .addShutdownHook(Thread {
                println("[VPNLauncher] JVM shutting down, cleaning up...")
                cleanup()
            }
            )
    }

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

    override fun startVPN(isAuthEnabled: Boolean) {
        cleanup()

        Thread.sleep(1000)

        val proxyData = prefStore.proxyData
        val interfaceName = "KMPVPN_${System.currentTimeMillis()}"

        "--> proxyData: $proxyData".e

        // 1. Prepare Config
        val config = ConfigBuilder.getSocks5DesktopConfig(
            host = proxyData.host,
            port = proxyData.port,
            username = proxyData.username,
            password = proxyData.password,
            logLevel = "debug",
            interfaceName = interfaceName,
            isDefaultRouteVPN = prefStore.routeAllApps,
            allowedPackages = prefStore.allowedAppsNamesOnly
        )
        val configFile = File(workDir, "config.json").apply { writeText(config) }

        // 2. Extract binaries from resources
        val exeFile = extractResource(resourceName = SingboxExe)
        extractResource(resourceName = WintunDll)

        // 3. Launch
        // We launch sing-box directly because the app itself is already running with admin privileges.
        // This allows us to capture stdout/stderr which 'Start-Process' would detach.
        val pb = ProcessBuilder(
            exeFile.absolutePath,
            "run",
            "-c",
            configFile.absolutePath
        )
        pb.directory(workDir)
        pb.redirectErrorStream(true)
        vpnProcess = pb.start()

        signalManager.onConnected()

        val logFile = File(workDir, "logs.txt")
        debugStreamHandler = Thread {
            try {
                vpnProcess?.inputStream?.bufferedReader()?.use { reader ->
                    logFile.bufferedWriter().use { writer ->
                        while (!Thread.currentThread().isInterrupted) {
                            val line = reader.readLine() ?: break
                            val logLine = "[SINGBOX] $line"
                            println(logLine)
                            writer.write(logLine)
                            writer.newLine()
                            writer.flush()
                        }
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
            // 1. Force kill the sing-box process tree
            ProcessBuilder("taskkill", "/F", "/IM", "sing-box.exe", "/T").start().waitFor()

            // 2. Clear DNS first (prevents routing lockups)
            ProcessBuilder(
                "netsh",
                "interface",
                "ip",
                "set",
                "dns",
                "name=\"Wi-Fi\"",
                "source=dhcp"
            ).start().waitFor()

            // 3. Nuclear Cleanup: Use PowerShell to remove all KMPVPN adapters
            val psCommand =
                "Get-NetAdapter -Name 'KMPVPN_*' -ErrorAction SilentlyContinue | Remove-NetAdapter -Confirm:\$false"
            ProcessBuilder("powershell", "-Command", psCommand).start().waitFor()
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