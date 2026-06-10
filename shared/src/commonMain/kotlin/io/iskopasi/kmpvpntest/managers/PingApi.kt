package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.Ipify
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get

interface PingApi {
    suspend fun connect(proxyData: ProxyData): Boolean
}

class PingApiImpl : PingApi {
    override suspend fun connect(proxyData: ProxyData): Boolean {
        val client = getClient(proxyData)

        return try {
            val response = client.get(Ipify) {
                timeout {
                    connectTimeoutMillis = 5000
                    requestTimeoutMillis = 5000
                }
            }

            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        } finally {
            client.close()
        }
    }

}

expect fun getClient(proxyData: ProxyData): HttpClient