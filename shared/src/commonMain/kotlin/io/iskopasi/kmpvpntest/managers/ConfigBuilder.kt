package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.api.isAndroid
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
        protected var filterList: Set<String>? = null

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

        fun setFilterList(filterList: Set<String>?): Builder {
            this.filterList = filterList
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

        protected fun getRouteBlockInner(
            finalRoute: String,
            rules: List<Map<String, Any>>,
            autoDetectInterface: Boolean = false,
        ):
                Map<String, Any> {
            return mapOf(
                "auto_detect_interface" to autoDetectInterface,
                "rules" to rules,
                "final" to finalRoute
            )
        }

        protected open fun getAppListBlock(
            appList: Set<String>?,
            outbound: String,
            usePackageNames: Boolean = true
        ): Map<String, Any> {
            val key = if (usePackageNames) "package_name" else "process_name"

            return mapOf(
                key to (appList?.toList() ?: emptyList<String>()),
                "outbound" to outbound
            )
        }

        protected open fun getDnsFilterBlock(filterList: Set<String>?): Map<String, Any> {
            return mapOf(
                "domain_suffix" to (filterList?.toList() ?: emptyList<String>()),
                "outbound" to "block"
            )
        }

        protected fun getHijackDnsBlock(): Map<String, Any> {
            return mapOf(
                "port" to 53,
                "action" to "hijack-dns"
            )
        }

        protected fun getSniffingBlock(): Map<String, Any> {
            return mapOf(
                "action" to "sniff",
                "timeout" to "1s"
            )
        }

        protected open fun getOutboundItemDesktop() =
            mapOf(
                "type" to "direct",
                "tag" to "direct"
            )

        protected open fun getOutboundItemAndroid() =
            mapOf(
                "type" to "direct",
                "tag" to "direct",
                "domain_strategy" to "prefer_ipv4",
                "bind_interface" to "en"
            )

        protected fun getFilterBlock() =
            mapOf(
                "type" to "block",
                "tag" to "block"
            )

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

        protected open fun getOutboundBlock(
            host: String,
            port: Int,
            username: String?,
            password: String?,
            hasFilterBlock: Boolean,
        ): List<Map<String, Any>> =
            mutableListOf<Map<String, Any>>(
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
                    ),
                if (isAndroid)
                    getOutboundItemAndroid()
                else
                    getOutboundItemDesktop()
            ).apply {
                if (hasFilterBlock)
                    add(getFilterBlock())
            }

        protected fun getRouteBlock(
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
                        hasFilterBlock = filterList != null
                    ).toJsonElement()
                )

                put(
                    "route", getRouteBlock(
                        routeAllApps = routeAllApps,
                        appList = appList,
                        filterList = filterList,
                        usePackageNames = isAndroid
                    ).toJsonElement()
                )
            }

            return configMap.toString()
        }
    }

    fun getConfig(
        host: String,
        port: String,
        username: String?,
        password: String?,
        logLevel: String?,
        interfaceName: String = "en",
        allowedPackages: Set<String> = emptySet(),
        routeAllAppsIntoVPN: Boolean,
        filterList: Set<String>
    ): String
}

expect fun getConfigBuilder(): IConfigBuilder
