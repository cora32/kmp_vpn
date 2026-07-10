package io.iskopasi.kmpvpntest.managers

import kotlinx.serialization.json.buildJsonObject


internal class DesktopConfigBuilder : IConfigBuilder {
    private open class Builder : IConfigBuilder.Builder() {
        override fun getDnsServersBlock() = mapOf(
            "servers" to listOf(
                mapOf(
                    "type" to "https",
                    "tag" to "cloudflare",
                    "server" to "1.1.1.1",
                    "path" to "/dns-query",
                    "detour" to "proxy"
                )
            ),
            "rules" to listOf(
                mapOf(
                    "server" to "cloudflare"
                )
            )
        )

        override fun getInboundBlock(interfaceName: String): List<Map<String, Any>> {
            return listOf(
                mapOf(
                    "type" to "tun",
                    "tag" to "tun-in",
                    "stack" to "gvisor",
                    "interface_name" to interfaceName,
                    "address" to listOf("172.19.0.1/30"),
                    "mtu" to 1400,
                    "auto_route" to true,
                    "strict_route" to true
                )
            )
        }

        override fun getRouteBlock(
            routeAllApps: Boolean,
            appList: Set<String>?,
            filterList: Set<String>?,
            usePackageNames: Boolean
        ): Map<String, Any> {
            val rules = mutableListOf<Map<String, Any>>(
                getHijackDnsBlock() // Hijack first
            ).apply {
                // Sniffing and blocking after
                if (filterList?.isEmpty() == false) {
                    add(getSniffingBlock())
                    add(getDnsFilterBlock(filterList = filterList))
                }

                // And then split routing
                if (!routeAllApps && appList?.isEmpty() == false)
                    add(
                        getAppListBlock(
                            appList = appList,
                            outbound = Routes.Proxy.name.lowercase(),
                            usePackageNames = usePackageNames
                        )
                    )
            }

            return getRouteBlockInner(
                finalRoute = Routes.Direct.name.lowercase(),
                rules,
                autoDetectInterface = true
            )
        }

        override fun build(): String {
            when {
                host == null -> throw Exception("Host must be set")
                port == null -> throw Exception("Port must be set")
                inboundInterface == null -> throw Exception("Bind interface must be set")
            }

            configMap = buildJsonObject {
                logLevel?.let { logLevel ->
                    put("log", getLogBlock(logLevel = logLevel).toJsonElement())
                }

                put("dns", getDnsServersBlock().toJsonElement())
                put("inbounds", getInboundBlock(interfaceName = inboundInterface!!).toJsonElement())
                put(
                    "outbounds", getOutboundBlock(
                        host = host!!,
                        port = port!!,
                        username = username,
                        password = password,
                        hasFilterBlock = filterList?.isNotEmpty() == true
                    ).toJsonElement()
                )

                put(
                    "route", getRouteBlock(
                        routeAllApps = routeAllApps,
                        appList = appList,
                        filterList = filterList,
                        usePackageNames = false
                    ).toJsonElement()
                )
            }

            return configMap.toString()
        }
    }

    override fun getConfig(
        host: String,
        port: String,
        username: String?,
        password: String?,
        logLevel: String?,
        interfaceName: String,
        allowedPackages: Set<String>,
        routeAllAppsIntoVPN: Boolean,
        filterList: Set<String>
    ): String {
        return Builder()
            .setLog(logLevel)
            .setHost(host)
            .setPort(port)
            .setUsername(username)
            .setPassword(password)
            .setPassword(password)
            .setInboundInterface(interfaceName)
            .setRouteAllApps(routeAllAppsIntoVPN)
            .setAppList(allowedPackages)
            .setFilterList(filterList)
            .build()
    }
}

actual fun getConfigBuilder(): IConfigBuilder = DesktopConfigBuilder()