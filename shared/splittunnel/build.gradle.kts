import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    androidLibrary {
        namespace = "io.iskopasi.splittunnel"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.utils)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)

            api("com.arkivanov.decompose:decompose:3.5.0")
            api("com.arkivanov.essenty:lifecycle:2.5.0")
            implementation("com.arkivanov.decompose:extensions-compose:3.5.0")

            // Koin
            api("io.insert-koin:koin-core:3.2.0")

            // Coil 3
            api("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
            api("io.coil-kt.coil3:coil-network-okhttp:3.0.0-rc01")
        }
        jvmMain.dependencies {
        }
    }
}
