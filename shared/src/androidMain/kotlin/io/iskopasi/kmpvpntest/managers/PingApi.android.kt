package io.iskopasi.kmpvpntest.managers

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy

actual fun getClient(proxyData: ProxyData): HttpClient {
    // SOCKS5 authentication on Android/JVM requires a global Authenticator
    if (proxyData.username.isNotEmpty()) {
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                if (requestingHost == proxyData.host && requestingPort == proxyData.port.toInt()) {
                    return PasswordAuthentication(
                        proxyData.username,
                        proxyData.password.toCharArray()
                    )
                }
                return super.getPasswordAuthentication()
            }
        })
    }

    return HttpClient(OkHttp) {
        engine {
            proxy = Proxy(
                Proxy.Type.SOCKS,
                InetSocketAddress(proxyData.host, proxyData.port.toInt())
            )
        }
    }
}
