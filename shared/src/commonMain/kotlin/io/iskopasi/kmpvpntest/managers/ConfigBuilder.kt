package io.iskopasi.kmpvpntest.managers

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

enum class Routes {
    Proxy,
    Direct
}

interface IConfigBuilder {
    open class Builder {
        protected var configMap = emptyMap<String, Any>()
        protected var host: String? = null
        protected var port: Int? = null
        protected var username: String? = null
        protected var password: String? = null
        protected var bindInterface: String? = null
        protected var logLevel: String? = null
        protected var routeAllApps: Boolean = true
        protected var appList: Set<String>? = null

        protected open fun getDnsServersBlock() = mapOf(
            "servers" to listOf(
                mapOf(
                    "tag" to "google-dns",
                    "address" to "tcp://8.8.8.8",
                    "detour" to "proxy"
                )
            )
        )

        protected fun getLogBlock(logLevel: String) = mapOf(
            "level" to logLevel
        )

        fun setLog(logLevel: String?): Builder {
            this.logLevel = logLevel
            return this
        }

        fun setInterface(interfaceName: String): Builder {
            this.bindInterface = interfaceName
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

        protected fun Any?.toJsonElement(): JsonElement = when (this) {
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

        open fun build(): String {
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

        protected fun getSocks5WithAuthBlock(
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

        protected fun getSocks5Block(
            host: String,
            port: Int
        ) = mapOf(
            "type" to "socks",
            "tag" to "proxy",
            "server" to host,
            "server_port" to port,
            "version" to "5"
        )


        protected open fun getOutboundItem() =
            mapOf(
                "type" to "direct",
                "tag" to "direct",
                "domain_strategy" to "prefer_ipv4",
                "bind_interface" to "en"
            )

        protected open fun getOutboundBlock(
            host: String,
            port: Int,
            username: String?,
            password: String?,
        ): List<Map<String, Any>> =
            listOf<Map<String, Any>>(
                getOutboundItem(),
                if (username != null && password != null)
                    getSocks5WithAuthBlock(
                        host = host,
                        port = port,
                        username = username,
                        password = password,
                    )
                else
                    getSocks5Block(
                        host = host,
                        port = port,
                    )
            )

        protected open fun getAppListBlock(
            appList: Set<String>?,
            outbound: String
        ): Map<String, Any> {
            return mapOf(
                "package_name" to (appList?.toList() ?: emptyList<String>()),
                "outbound" to outbound
            )
        }

        protected fun getHijackDnsBlock(): Map<String, Any> {
            return mapOf(
                "port" to 53,
                "action" to "hijack-dns"
            )
        }

        protected open fun getRouteBlockInner(
            finalRoute: String,
            vararg rules: Map<String, Any>
        ):
                Map<String, Any> {
            return mapOf(
                "rules" to rules.toList(),
                "final" to finalRoute
            )
        }

        protected open fun getRouteBlock(
            routeAllApps: Boolean,
            appList: Set<String>?
        ): Map<String, Any> {
            return if (routeAllApps) {
                getRouteBlockInner(
                    finalRoute = Routes.Proxy.name.lowercase(),
                    getHijackDnsBlock()
                )
            } else {
                getRouteBlockInner(
                    finalRoute = Routes.Proxy.name.lowercase(),
                    getAppListBlock(appList = appList, outbound = Routes.Proxy.name.lowercase()),
                    getHijackDnsBlock()
                )
            }
        }

        open fun getInboundBlock(interfaceName: String) = listOf(
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

    }

    fun getConfig(
        host: String,
        port: String,
        username: String?,
        password: String?,
        logLevel: String?,
        interfaceName: String = "en",
        allowedPackages: Set<String> = emptySet(),
        routeAllAppsIntoVPN: Boolean
    ): String
}

expect fun getConfigBuilder(): IConfigBuilder
