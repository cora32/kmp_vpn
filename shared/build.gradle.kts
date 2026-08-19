import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.kotlinSerialization)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.iskopasi.kmpvpntest.generated.resources"
}

kotlin {
    jvm()

    android {
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
            api(libs.koin.android)

            // Singbox
            api(files("libs/singbox.aar"))

            //OkHttp
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            api(projects.utils)
            api(projects.splittunnel)
            api(projects.dnsFilter)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            api(libs.compose.material3)
            implementation(libs.compose.ui)
            api(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin
            api(libs.koin.core)
            api(libs.koin.viewmodel)
            api(libs.koin.compose.viewmodel)

            // Json
            api(libs.kotlinx.serialization.json)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.ktor.client.okhttp)

            // Nav3
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.jetbrains.navigation3.ui)
        }
        jvmMain.dependencies {
            implementation(libs.logback.classic)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}