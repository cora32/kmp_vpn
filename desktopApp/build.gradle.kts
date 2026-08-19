import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)
    implementation(projects.splittunnel)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.components.resources)
}

compose.desktop {
    application {
        mainClass = "io.iskopasi.kmpvpntest.MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ProxyVPN"
            packageVersion = "1.0.0"
            vendor = "Iskopasi"
            description = "A cross-platform VPN application"

            windows {
                shortcut = true
                menu = true
                menuGroup = "ProxyVPN"
                upgradeUuid =
                    "68c9f53e-862d-4566-880a-9d62d08a123a" // Random static UUID for upgrades
            }
        }
    }
}