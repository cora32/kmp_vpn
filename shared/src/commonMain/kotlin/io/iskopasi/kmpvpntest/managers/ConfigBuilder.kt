package io.iskopasi.kmpvpntest.managers

private const val hostPlaceholder = "%HOST%"
private const val portPlaceholder = "%PORT%"
private const val usernamePlaceholder = "%USERNAME%"
private const val passwordPlaceholder = "%PASSWORD%"
private const val logPlaceholder = "%LOG_LEVEL%"
private const val allowedPackagesPlaceholder = "%APPS%"
private const val interfaceNamePlaceholder = "%INTERFACE_NAME%"

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

val allowAllAppsDesktop = """
{
  "log": {
    "level": "trace"
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
    "rules": [
      {
        "action": "hijack-dns",
        "port": 53
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

        fun getSocks5DesktopConfig(
            host: String,
            port: String,
            username: String,
            password: String,
            logLevel: String,
            interfaceName: String,
            allowedPackages: Set<String> = emptySet(),
            routeAllAppsIntoVPN: Boolean
        ): String {
            return allowAllAppsDesktop
                .replace(hostPlaceholder, host)
                .replace(portPlaceholder, port)
                .replace(usernamePlaceholder, username)
                .replace(passwordPlaceholder, password)
                .replace(logPlaceholder, logLevel)
                .replace(interfaceNamePlaceholder, interfaceName)
        }
    }
}
