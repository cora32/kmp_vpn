# Basic ProGuard rules for Compose Desktop
-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn sun.awt.**
-dontwarn com.sun.awt.**
-dontwarn org.jetbrains.skiko.**
-dontwarn org.jetbrains.skia.**

# Keep the main entry point
-keep class io.iskopasi.kmpvpntest.MainKt {
    public static void main(java.lang.String[]);
}

# Keep Koin
-keep class org.koin.** { *; }

# Keep Coil
-keep class coil3.** { *; }

# Keep Kotlin Serialization
-keepattributes *Annotation*, EnclosingMethod, Signature
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    *** Companion;
    *** $serializer;
}

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# VPN related
-keep class io.iskopasi.kmpvpntest.managers.VPNLauncher { *; }
