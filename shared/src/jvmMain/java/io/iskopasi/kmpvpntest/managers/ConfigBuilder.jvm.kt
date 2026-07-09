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

//        override fun getOutboundItem() =
//            mapOf(
//                "type" to "direct",
//                "tag" to "direct"
//            )
//
//        override fun getOutboundBlock(
//            host: String,
//            port: Int,
//            username: String?,
//            password: String?,
//            hasFilterBlock: Boolean
//        ): List<Map<String, Any>> =
//            listOf(
//                if (username != null && password != null)
//                    getSocks5WithAuthBlock(
//                        host = host,
//                        port = port,
//                        username = username,
//                        password = password,
//                    )
//                else
//                    getSocks5Block(
//                        host = host,
//                        port = port,
//                    ),
//
//                getOutboundItem(),
//            )


//        override fun getAppListBlock(appList: Set<String>?, outbound: String): Map<String, Any> {
//            return mapOf(
//                "process_name" to (appList?.toList() ?: emptyList<String>()),
//                "outbound" to outbound
//            )
//        }

//        override fun getRouteBlock(
//            routeAllApps: Boolean,
//            appList: Set<String>?,
//            filterList: Set<String>?
//        ): Map<String, Any> {
//            val rules = mutableListOf<Map<String, Any>>().apply {
//                when {
//                    !routeAllApps && appList?.isEmpty() == false -> add(getAppListBlock(appList = appList, outbound = Routes.Proxy.name.lowercase()))
//                    filterList?.isEmpty() == false -> add(getDnsFilterBlock(filterList = filterList))
//                }
//
//                add(getHijackDnsBlock())
//            }
//
//            return getRouteBlockInner(
//                finalRoute = Routes.Direct.name.lowercase(),
//                rules,
//                autoDetectInterface = true
//            )
//        }

        override fun build(): String {
            when {
                host == null -> throw Exception("Host must be set")
                port == null -> throw Exception("Port must be set")
                bindInterface == null -> throw Exception("Bind interface must be set")
            }

            configMap = buildJsonObject {
                logLevel?.let { logLevel ->
                    put("log", getLogBlock(logLevel = logLevel).toJsonElement())
                }

                put("dns", getDnsServersBlock().toJsonElement())
                put("inbounds", getInboundBlock(interfaceName = bindInterface!!).toJsonElement())
                put(
                    "outbounds", getOutboundBlock(
                        host = host!!,
                        port = port!!,
                        username = username,
                        password = password,
                        hasFilterBlock = filterList != null
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
            .setInterface(interfaceName)
            .setRouteAllApps(routeAllAppsIntoVPN)
            .setAppList(allowedPackages)
            .setFilterList(filterList)
            .build()
    }
}

actual fun getConfigBuilder(): IConfigBuilder = DesktopConfigBuilder()