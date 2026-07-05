package io.iskopasi.kmpvpntest.managers

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


internal class ConfigBuilder : IConfigBuilder {
    class Builder {
        private var configMap = emptyMap<String, Any>()
        private var host: String? = null
        private var port: Int? = null
        private var username: String? = null
        private var password: String? = null
        private var logLevel: String? = null
        private var routeAllApps: Boolean = true
        private var appList: Set<String>? = null

        private val dnsBlock = mapOf(
            "servers" to listOf(
                mapOf(
                    "tag" to "google-dns",
                    "address" to "tcp://8.8.8.8",
                    "detour" to "proxy"
                )
            )
        )
        private val inboundBlock = listOf(
            mapOf(
                "type" to "tun",
                "stack" to "gvisor",
                "address" to listOf("172.19.0.1/24"),
                "mtu" to 1500,
                "auto_route" to true,
                "strict_route" to true,
                "sniff" to true
            )
        )

        private val outboundBlock =
            mapOf(
                "type" to "direct",
                "tag" to "direct-out",
                "domain_strategy" to "prefer_ipv4",
                "bind_interface" to "en0"
            )

        private fun getLogBlock(logLevel: String) = mapOf(
            "level" to logLevel
        )

        fun setLog(logLevel: String?): Builder {
            this.logLevel = logLevel
            return this
        }

        fun setHost(host: String?): Builder {
            this.host = host
            return this
        }

        fun setPort(port: String?): Builder {
            this.port = port?.toInt()
            return this
        }

        fun setUsername(username: String?): Builder {
            this.username = username
            return this
        }

        fun setPassword(password: String?): Builder {
            this.password = password
            return this
        }

        fun setRouteAllApps(routeAllApps: Boolean): Builder {
            this.routeAllApps = routeAllApps
            return this
        }

        fun setAppList(appList: Set<String>?): Builder {
            this.appList = appList
            return this
        }

        private fun Any?.toJsonElement(): JsonElement = when (this) {
            null -> JsonNull
            is JsonElement -> this
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is String -> JsonPrimitive(this)
            is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
            is Map<*, *> -> buildJsonObject {
                this@toJsonElement.forEach { (k, v) ->
                    put(k.toString(), v.toJsonElement())
                }
            }

            else -> JsonPrimitive(this.toString())
        }

        fun build(): String {
            if (host == null || port == null)
                throw Exception("Host and port must be set")

            configMap = buildJsonObject {
                logLevel?.let { logLevel ->
                    put("log", getLogBlock(logLevel = logLevel).toJsonElement())
                }

                put("dns", dnsBlock.toJsonElement())
                put("inbounds", inboundBlock.toJsonElement())
                put(
                    "outbounds", getOutboundBlock(
                        host = host!!,
                        port = port!!,
                        username = username,
                        password = password
                    ).toJsonElement()
                )

                put(
                    "route", getRouteBlock(
                        routeAllApps = routeAllApps,
                        appList = appList
                    ).toJsonElement()
                )
            }

            return configMap.toString()
        }

        private fun getSocks5WithAuthBlock(
            host: String,
            port: Int,
            username: String,
            password: String
        ) = mapOf(
            "type" to "socks",
            "tag" to "proxy",
            "server" to host,
            "server_port" to port,
            "version" to "5",
            "username" to username,
            "password" to password
        )

        private fun getSocks5Block(
            host: String,
            port: Int
        ) = mapOf(
            "type" to "socks",
            "tag" to "proxy",
            "server" to host,
            "server_port" to port,
            "version" to "5"
        )

        private fun getOutboundBlock(
            host: String,
            port: Int,
            username: String?,
            password: String?
        ): List<Map<String, Any>> =
            mutableListOf<Map<String, Any>>().apply {
                add(outboundBlock)
                if (username != null && password != null)
                    add(
                        getSocks5WithAuthBlock(
                            host = host,
                            port = port,
                            username = username,
                            password = password,
                        )
                    )
                else add(
                    getSocks5Block(
                        host = host,
                        port = port,
                    )
                )
            }

        private fun getAppListBlock(appList: Set<String>?): Map<String, Any> {
            return mapOf(
                "package_name" to (appList?.toList() ?: emptyList<String>()),
                "outbound" to "proxy-out"
            )
        }

        private fun getHijackDnsBlock(): Map<String, Any> {
            return mapOf(
                "port" to 53,
                "action" to "hijack-dns"
            )
        }

        private fun getRouteBlockInner(vararg rules: Map<String, Any>):
                Map<String, Any> {
            return mapOf(
                "rules" to rules.toList(),
                "final" to "proxy"
            )
        }

        private fun getRouteBlock(
            routeAllApps: Boolean,
            appList: Set<String>?
        ): Map<String, Any> {
            return if (routeAllApps) {
                getRouteBlockInner(getHijackDnsBlock())
            } else {
                getRouteBlockInner(
                    getAppListBlock(appList = appList),
                    getHijackDnsBlock()
                )
            }
        }
    }

    override fun getConfig(
        host: String,
        port: String,
        username: String?,
        password: String?,
        logLevel: String?,
        allowedPackages: Set<String>,
        routeAllAppsIntoVPN: Boolean
    ): String {
        return Builder()
            .setLog(logLevel)
            .setHost(host)
            .setPort(port)
            .setUsername(username)
            .setPassword(password)
            .setRouteAllApps(routeAllAppsIntoVPN)
            .setAppList(allowedPackages)
            .build()
    }

    companion object {
        fun getSocks5Config(
            host: String,
            port: String,
            username: String?,
            password: String?,
            logLevel: String,
            allowedPackages: Set<String> = emptySet(),
            routeAllAppsIntoVPN: Boolean
        ): String {
            return Builder()
                .setLog(logLevel)
                .setHost(host)
                .setPort(port)
                .setUsername(username)
                .setPassword(password)
                .setRouteAllApps(routeAllAppsIntoVPN)
                .setAppList(allowedPackages)
                .build()
        }

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