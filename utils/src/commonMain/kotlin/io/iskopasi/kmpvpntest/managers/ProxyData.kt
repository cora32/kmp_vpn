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

        fun parse(text: String): ProxyData? {
            val trimmed = text.trim()
            // Regex patterns for the different formats
            val patterns = listOf(
                // socks5://username:password@host:port
                Regex("""^socks5://([^:@]+):([^:@]+)@([^:/]+):(\d+)$"""),
                // username:password@host:port
                Regex("""^([^:@]+):([^:@]+)@([^:/]+):(\d+)$"""),
                // socks5://host:port
                Regex("""^socks5://([^:/]+):(\d+)$"""),
                // host:port
                Regex("""^([^:/]+):(\d+)$""")
            )

            for ((index, regex) in patterns.withIndex()) {
                val match = regex.find(trimmed) ?: continue
                val groups = match.groupValues

                return when (index) {
                    0 -> ProxyData(
                        username = groups[1],
                        password = groups[2],
                        host = groups[3],
                        port = groups[4]
                    )

                    1 -> ProxyData(
                        username = groups[1],
                        password = groups[2],
                        host = groups[3],
                        port = groups[4]
                    )

                    2 -> ProxyData(
                        host = groups[1],
                        port = groups[2],
                        username = "",
                        password = ""
                    )

                    3 -> ProxyData(
                        host = groups[1],
                        port = groups[2],
                        username = "",
                        password = ""
                    )

                    else -> null
                }
            }

            // Fallback: assume it's just a host if no port/protocol found
            if (trimmed.isNotEmpty() && !trimmed.contains(" ")) {
                return ProxyData(host = trimmed, port = "", username = "", password = "")
            }

            return null
        }
    }
}
