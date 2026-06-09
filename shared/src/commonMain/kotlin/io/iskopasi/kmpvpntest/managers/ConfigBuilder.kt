package io.iskopasi.kmpvpntest.managers

private const val hostPlaceholder = "%HOST%"
private const val portPlaceholder = "%PORT%"
private const val usernamePlaceholder = "%USERNAME%"
private const val passwordPlaceholder = "%PASSWORD%"
private const val logPlaceholder = "%LOG_LEVEL%"
private const val allowedPackagesPlaceholder = "%APPS%"

val allowAllApps = """
    {
                      "log": {
                        "level": "$logPlaceholder"
                      },
                      "dns": {
                        "servers": [
                          {
                            "tag": "google-dns",
                            "address": "tcp://8.8.8.8",
                            "detour": "proxy"
                          }
                        ]
                      },
                      "inbounds": [
                        {
                          "type": "tun",
                          "stack": "gvisor",
                          "address": ["172.19.0.1/24"],
                          "mtu": 1500,
                          "auto_route": true,
                          "strict_route": true,
                                  "sniff": true
                        }
                      ],
                      "outbounds": [
                        {
                          "type": "direct",
                          "tag": "direct-out",
                          "domain_strategy": "prefer_ipv4",
                          "bind_interface": "en0"
                        },
                        {
                          "type": "socks",
                          "tag": "proxy",
                          "server": "$hostPlaceholder",
                          "server_port": $portPlaceholder,
                          "version": "5",
                        "username": "$usernamePlaceholder",
                        "password": "$passwordPlaceholder"
                        }
                      ],
                      "route": {
                        "rules": [
                          {
                            "port": 53,
                            "action": "hijack-dns"
                          }
                        ],
                        "final": "proxy"
                      }
                    }
""".trimIndent()

val allowSelectedApps = """
    {
                      "log": {
                        "level": "$logPlaceholder"
                      },
                      "dns": {
                        "servers": [
                          {
                            "tag": "google-dns",
                            "address": "tcp://8.8.8.8",
                            "detour": "proxy"
                          }
                        ]
                      },
                      "inbounds": [
                        {
                          "type": "tun",
                          "stack": "gvisor",
                          "address": ["172.19.0.1/24"],
                          "mtu": 1500,
                          "auto_route": true,
                          "strict_route": true,
                                  "sniff": true
                        }
                      ],
                      "outbounds": [
                        {
                          "type": "direct",
                          "tag": "direct-out",
                          "domain_strategy": "prefer_ipv4",
                          "bind_interface": "en0"
                        },
                        {
                          "type": "socks",
                          "tag": "proxy",
                          "server": "$hostPlaceholder",
                          "server_port": $portPlaceholder,
                          "version": "5",
                        "username": "$usernamePlaceholder",
                        "password": "$passwordPlaceholder"
                        }
                      ],
                      "route": {
                        "rules": [
                          {
                            "package_name": [$allowedPackagesPlaceholder],
                            "outbound": "proxy-out"
                          },
                          {
                            "port": 53,
                            "action": "hijack-dns"
                          }
                        ],
                        "final": "proxy"
                      }
                    }
""".trimIndent()

class ConfigBuilder {
    companion object {
        fun getSocks5Config(
            host: String,
            port: String,
            username: String,
            password: String,
            logLevel: String,
            allowedPackages: Set<String> = emptySet(),
            routeAllAppsIntoVPN: Boolean
        ): String {
            if (routeAllAppsIntoVPN)
                return allowAllApps
                    .replace(hostPlaceholder, host)
                    .replace(portPlaceholder, port)
                    .replace(usernamePlaceholder, username)
                    .replace(passwordPlaceholder, password)
                    .replace(logPlaceholder, logLevel)
            else {
                val packagesJson = allowedPackages.joinToString("\", \"", "\"", "\"")

                return allowSelectedApps
                    .replace(hostPlaceholder, host)
                    .replace(portPlaceholder, port)
                    .replace(usernamePlaceholder, username)
                    .replace(passwordPlaceholder, password)
                    .replace(logPlaceholder, logLevel)
                    .replace(allowedPackagesPlaceholder, packagesJson)
            }
        }
    }
}