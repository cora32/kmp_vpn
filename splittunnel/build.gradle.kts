import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.iskopasi.splittunnel.generated.resources"
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
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.utils)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            api(libs.compose.components.resources)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.compose.viewmodel)

            // Coil 3
            api("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
            api("io.coil-kt.coil3:coil-network-okhttp:3.0.0-rc01")
        }
        androidMain.dependencies {
            implementation(libs.compose.components.resources)
        }
        jvmMain.dependencies {
        }
    }
}
