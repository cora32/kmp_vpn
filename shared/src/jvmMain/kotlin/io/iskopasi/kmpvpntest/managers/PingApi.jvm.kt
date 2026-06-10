package io.iskopasi.kmpvpntest.managers

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO

actual fun getClient(proxyData: ProxyData): HttpClient {
    // SOCKS5 authentication for CIO on JVM is typically handled via System properties
    if (proxyData.username.isNotEmpty()) {
        System.setProperty("java.net.socks.username", proxyData.username)
        System.setProperty("java.net.socks.password", proxyData.password)
    }

    return HttpClient(CIO) {
        engine {
            proxy = ProxyBuilder.socks(proxyData.host, proxyData.port.toInt())
        }
    }
}
