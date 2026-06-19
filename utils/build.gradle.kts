plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "io.iskopasi.kmpvpntest.utils"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            //Koin
            api("io.insert-koin:koin-core:3.2.0")
        }
        androidMain.dependencies {
            api(libs.androidx.core.ktx)
        }
    }
}
