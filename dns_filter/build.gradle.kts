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
    packageOfResClass = "io.iskopasi.dns_filter.generated.resources"
}

kotlin {
    jvm()

    androidLibrary {
        namespace = "io.iskopasi.dns_filter"
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
        commonMain.dependencies {
            api(projects.utils)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            api(libs.compose.components.resources)

            // Koin
            api("io.insert-koin:koin-core:3.2.0")

            // Decompose
            api(libs.decompose)
            api(libs.essenty.lifecycle)
            implementation(libs.decompose.compose)
        }
        androidMain.dependencies {
            implementation(libs.compose.components.resources)
        }
        jvmMain.dependencies {
        }
    }
}
