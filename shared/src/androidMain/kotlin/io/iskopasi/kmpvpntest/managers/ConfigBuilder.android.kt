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
        routeAllAppsIntoVPN: Boolean,
        filterList: Set<String>
    ): String {
        return IConfigBuilder.Builder()
            .setLog(logLevel)
            .setHost(host)
            .setPort(port)
            .setUsername(username)
            .setPassword(password)
            .setInboundInterface(interfaceName)
            .setRouteAllApps(routeAllAppsIntoVPN)
            .setAppList(allowedPackages)
            .setFilterList(filterList)
            .build()
    }
}

actual fun getConfigBuilder(): IConfigBuilder = ConfigBuilder()