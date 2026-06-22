import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    jvm()
    
    androidLibrary {
       namespace = "io.iskopasi.kmpvpntest.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)

            // Koin
            api("io.insert-koin:koin-android:3.2.0")

            // Singbox
            api(files("libs/singbox.aar"))

            //OkHttp
            implementation("io.ktor:ktor-client-okhttp:3.5.0")
        }
        commonMain.dependencies {
            api(projects.utils)
            implementation(projects.shared.splittunnel)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            api(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Decompose
            api("com.arkivanov.decompose:decompose:3.5.0")
            api("com.arkivanov.essenty:lifecycle:2.5.0")
            implementation("com.arkivanov.decompose:extensions-compose:3.5.0")

            // Koin
            api("io.insert-koin:koin-core:3.2.0")

            // Json
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            // Ktor
            implementation("io.ktor:ktor-client-core:3.5.0")

            implementation("io.ktor:ktor-client-okhttp:3.5.0")

            // Nav3
            implementation("androidx.navigation3:navigation3-runtime:1.2.0-alpha04")
            implementation("androidx.navigation3:navigation3-ui:1.2.0-alpha04")
        }
        jvmMain.dependencies {
            implementation("ch.qos.logback:logback-classic:1.5.34")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}