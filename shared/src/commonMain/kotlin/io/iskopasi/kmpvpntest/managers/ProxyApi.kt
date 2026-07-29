package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.Ipify
import io.iskopasi.kmpvpntest.ProxyListUrl
import io.iskopasi.kmpvpntest.api.e
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Serializable
data class ProxyEntry(
    val ip: String,
    val port: Int,
    val proxy: String? = null,
    val protocol: String? = null
)

interface ProxyApi {
    suspend fun connect(proxyData: ProxyData, isCertCheckEnabled: Boolean): Boolean

    suspend fun fetchProxyList(): List<ProxyData>
}

class ProxyApiImpl : ProxyApi {
    private fun getClient(proxyData: ProxyData, isCertCheckEnabled: Boolean): HttpClient {
        // ... (existing implementation)
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


                // Disabling cert checks for testing purposes
                if (!isCertCheckEnabled) {
                    val trustAllCerts = object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) {
                        }

                        override fun checkServerTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }

                    val sslContext = SSLContext.getInstance("SSL")
                    sslContext.init(null, arrayOf(trustAllCerts), SecureRandom())

                    config {
                        sslSocketFactory(sslContext.socketFactory, trustAllCerts)
                        hostnameVerifier { _, _ -> true }
                    }
                }
            }
        }
    }

    override suspend fun connect(proxyData: ProxyData, isCertCheckEnabled: Boolean): Boolean {
        val client = getClient(proxyData, isCertCheckEnabled = isCertCheckEnabled)

        "[PingApi] Connecting to ${proxyData.host}:${proxyData.port}".e

        client.use { client ->
            val response = client.get(Ipify) {
                timeout {
                    connectTimeoutMillis = 5000
                    requestTimeoutMillis = 5000
                }
            }

            "[PingApi] Response: ${response.bodyAsText()}".e

            return true
        }
    }

    override suspend fun fetchProxyList(): List<ProxyData> {
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        client.use { client ->
            val entries = client.get(ProxyListUrl).body<List<ProxyEntry>>()

            return entries.map { entry ->
                ProxyData(
                    host = entry.ip,
                    port = entry.port.toString(),
                    username = "",
                    password = ""
                )
            }
        }
    }

}