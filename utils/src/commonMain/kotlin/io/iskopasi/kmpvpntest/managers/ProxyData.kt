package io.iskopasi.kmpvpntest.managers

import kotlinx.serialization.Serializable

@Serializable
data class ProxyData(
    val host: String,
    val port: String,
    val username: String,
    val password: String
) {
    companion object {
        val empty = ProxyData(
            host = "",
            port = "",
            username = "",
            password = ""
        )
    }
}
