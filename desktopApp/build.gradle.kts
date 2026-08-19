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

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.iskopasi.kmpvpntest"
            packageVersion = "1.0.0"

            windows {
                // 'requestedExecutionLevel' is unresolved in this version of Compose Multiplatform (1.11.0).
                // If you need admin privileges, consider using a custom manifest or a third-party plugin.
                // requestedExecutionLevel = "requireAdministrator"
            }
        }
    }
}