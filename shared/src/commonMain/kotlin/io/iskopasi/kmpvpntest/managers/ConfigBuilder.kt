package io.iskopasi.kmpvpntest.managers

import io.iskopasi.kmpvpntest.e

private const val hostPlaceholder = "%HOST%"
private const val portPlaceholder = "%PORT%"
private const val usernamePlaceholder = "%USERNAME%"
private const val passwordPlaceholder = "%PASSWORD%"
private const val logPlaceholder = "%LOG_LEVEL%"
private const val allowedPackagesPlaceholder = "%APPS%"
private const val interfaceNamePlaceholder = "%INTERFACE_NAME%"
private const val finalOutboundPlaceholder = "%FINAL_OUTBOUND%"
private const val rulesPlaceholder = "%RULES%"

val allowAllAppsAndroid = """
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

val allowSelectedAppsAndroid = """
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


val allowAllAppsDesktop = """
{
  "log": {
    "level": "$logPlaceholder"
  },
  "dns": {
    "servers": [
      {
        "type": "https",
        "tag": "cloudflare",
        "server": "1.1.1.1",
        "path": "/dns-query",
        "detour": "proxy"
      }
    ],
    "rules": [
      {
        "server": "cloudflare"
      }
    ]
  },
  "inbounds": [
    {
      "type": "tun",
      "tag": "tun-in",
      "interface_name": "$interfaceNamePlaceholder",
      "address": [
        "172.19.0.1/30"
      ],
      "auto_route": true,
      "strict_route": true,
      "stack": "gvisor",
      "mtu": 1400
    }
  ],
  "outbounds": [
    {
      "type": "socks",
      "tag": "proxy",
      "server": "$hostPlaceholder",
      "server_port": $portPlaceholder,
      "username": "$usernamePlaceholder",
      "password": "$passwordPlaceholder"
    },
    {
      "type": "direct",
      "tag": "direct"
    }
  ],
  "route": {
    "auto_detect_interface": true,
    "rules": [
      {
        "action": "hijack-dns",
        "port": 53
      }
      $rulesPlaceholder
    ],
    "final": "$finalOutboundPlaceholder"
  }
}
""".trimIndent()

val test = """
    {
      "log": {
        "level": "debug"
      },
      "dns": {
        "servers": [
          {
            "type": "https",
            "tag": "cloudflare",
            "server": "1.1.1.1",
            "path": "/dns-query",
            "detour": "proxy"
          }
        ],
        "rules": [
          {
            "server": "cloudflare"
          }
        ]
      },
      "inbounds": [
        {
          "type": "tun",
          "tag": "tun-in",
          "interface_name": "KMPVPN_1781447255910",
          "address": [
            "172.19.0.1/30"
          ],
          "auto_route": true,
          "strict_route": true,
          "stack": "gvisor",
          "mtu": 1400
        }
      ],
      "outbounds": [
        {
          "type": "socks",
          "tag": "proxy",
          "server": "45.39.15.76",
          "server_port": 6506,
          "username": "kwfioenv",
          "password": "vzt79p8cffy6"
        },
        {
          "type": "direct",
          "tag": "direct"
        }
      ],
      "route": {
        "auto_detect_interface": true,
        "rules": [
          {
            "action": "hijack-dns",
            "port": 53
          },
          {
            "process_name": [
              "firefox.exe"
            ],
            "outbound": "proxy"
          },
          {
            "process_name": [
              "vivaldi.exe"
            ],
            "outbound": "direct"
          }
        ],
        "final": "direct"
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
                return allowAllAppsAndroid
                    .replace(hostPlaceholder, host)
                    .replace(portPlaceholder, port)
                    .replace(usernamePlaceholder, username)
                    .replace(passwordPlaceholder, password)
                    .replace(logPlaceholder, logLevel)
            else {
                val packagesJson = allowedPackages.joinToString("\", \"", "\"", "\"")

                return allowSelectedAppsAndroid
                    .replace(hostPlaceholder, host)
                    .replace(portPlaceholder, port)
                    .replace(usernamePlaceholder, username)
                    .replace(passwordPlaceholder, password)
                    .replace(logPlaceholder, logLevel)
                    .replace(allowedPackagesPlaceholder, packagesJson)
            }
        }

        fun getSocks5DesktopConfig(
            host: String,
            port: String,
            username: String,
            password: String,
            logLevel: String,
            interfaceName: String,
            allowedPackages: Set<String> = emptySet(),
            isDefaultRouteVPN: Boolean
        ): String {
            val (finalOutbound, rules) = if (isDefaultRouteVPN) {
                "proxy" to ""
            } else {
                val packagesJson = allowedPackages.joinToString("\", \"", "\"", "\"")
                "direct" to """,
                    {
                        "process_name": [$packagesJson],
                        "outbound": "proxy"
                    }
                """.trimIndent()
            }

            "[ConfigBuilder] Default route: $finalOutbound; rules: $rules".e
            "[ConfigBuilder] allowedPackages: $allowedPackages; routeAllAppsIntoVPN: $isDefaultRouteVPN".e

            return allowAllAppsDesktop
                .replace(hostPlaceholder, host)
                .replace(portPlaceholder, port)
                .replace(usernamePlaceholder, username)
                .replace(passwordPlaceholder, password)
                .replace(logPlaceholder, logLevel)
                .replace(interfaceNamePlaceholder, interfaceName)
                .replace(finalOutboundPlaceholder, finalOutbound)
                .replace(rulesPlaceholder, rules)
        }
    }
}
