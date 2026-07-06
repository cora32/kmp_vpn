package io.iskopasi.kmpvpntest.managers


internal class ConfigBuilder : IConfigBuilder {
    override fun getConfig(
        host: String,
        port: String,
        username: String?,
        password: String?,
        logLevel: String?,
        interfaceName: String,
        allowedPackages: Set<String>,
        routeAllAppsIntoVPN: Boolean
    ): String {
        return IConfigBuilder.Builder()
            .setLog(logLevel)
            .setHost(host)
            .setPort(port)
            .setUsername(username)
            .setPassword(password)
            .setInterface(interfaceName)
            .setRouteAllApps(routeAllAppsIntoVPN)
            .setAppList(allowedPackages)
            .build()
    }

    companion object {

//        fun getSocks5DesktopConfig(
//            host: String,
//            port: String,
//            username: String,
//            password: String,
//            logLevel: String,
//            interfaceName: String,
//            allowedPackages: Set<String> = emptySet(),
//            isDefaultRouteVPN: Boolean
//        ): String {
//            val (finalOutbound, rules) = if (isDefaultRouteVPN) {
//                "proxy" to ""
//            } else {
//                val packagesJson = allowedPackages.joinToString("\", \"", "\"", "\"")
//                "direct" to """,
//                    {
//                        "process_name": [$packagesJson],
//                        "outbound": "proxy"
//                    }
//                """.trimIndent()
//            }
//
//            "[ConfigBuilder] Default route: $finalOutbound; rules: $rules".e
//            "[ConfigBuilder] allowedPackages: $allowedPackages; routeAllAppsIntoVPN: $isDefaultRouteVPN".e
//
//            return allowAllAppsDesktop
//                .replace(hostPlaceholder, host)
//                .replace(portPlaceholder, port)
//                .replace(usernamePlaceholder, username)
//                .replace(passwordPlaceholder, password)
//                .replace(logPlaceholder, logLevel)
//                .replace(interfaceNamePlaceholder, interfaceName)
//                .replace(finalOutboundPlaceholder, finalOutbound)
//                .replace(rulesPlaceholder, rules)
//        }
    }
}

actual fun getConfigBuilder(): IConfigBuilder = ConfigBuilder()