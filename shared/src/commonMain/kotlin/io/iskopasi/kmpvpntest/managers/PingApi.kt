package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.Ipify
import io.iskopasi.kmpvpntest.api.e
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy

interface PingApi {
    suspend fun connect(proxyData: ProxyData): Boolean
}

class PingApiImpl : PingApi {
    fun getClient(proxyData: ProxyData): HttpClient {
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
//                val trustAllCerts = object : X509TrustManager {
//                    override fun checkClientTrusted(
//                        chain: Array<out X509Certificate>?,
//                        authType: String?
//                    ) {
//                    }
//
//                    override fun checkServerTrusted(
//                        chain: Array<out X509Certificate>?,
//                        authType: String?
//                    ) {
//                    }
//
//                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//                }
//
//                val sslContext = SSLContext.getInstance("SSL")
//                sslContext.init(null, arrayOf(trustAllCerts), SecureRandom())
//
//                config {
//                    sslSocketFactory(sslContext.socketFactory, trustAllCerts)
//                    hostnameVerifier { _, _ -> true }
//                }
            }
        }
    }

    override suspend fun connect(proxyData: ProxyData): Boolean {
        val client = getClient(proxyData)

        "[PingApi] Connecting to ${proxyData.host}:${proxyData.port}".e

        return try {
            val response = client.get(Ipify) {
                timeout {
                    connectTimeoutMillis = 5000
                    requestTimeoutMillis = 5000
                }
            }

            "[PingApi] Response: ${response.bodyAsText()}".e

            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        } finally {
            client.close()
        }
    }

}