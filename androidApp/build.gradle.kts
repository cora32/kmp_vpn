import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)


    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)
    implementation(projects.shared.splittunnel)
    implementation(projects.shared.dnsFilter)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.components.resources)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "io.iskopasi.kmpvpntest"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.iskopasi.kmpvpntest"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    androidComponents {
        onVariants { variant ->
            val appName = "kmp_vpn_test"
            val versionName =
                variant.outputs.firstOrNull()?.versionName?.get() ?: defaultConfig.versionName
            val versionCode =
                variant.outputs.firstOrNull()?.versionCode?.get() ?: defaultConfig.versionCode

            // This is the name template used for the AAB and APK
            val newName = "${appName}_(${versionName})_(${versionCode})"

            // Set the base name for all outputs of this variant
            variant.outputs.forEach { _ ->
                // This property tells AGP what the 'base' of the filename should be
                base.archivesName.set(newName)
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}