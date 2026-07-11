-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-adaptclassstrings
-adaptresourcefilenames **.txt,**.xml
-adaptresourcefilecontents **.txt,**.xml

-keep class * extends androidx.room.RoomDatabase { <init>(); }

# Singbox / Gomobile rules
-keep class go.** { *; }
-keep class io.nekohasekai.libbox.** { *; }

# Preserve native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve attributes needed for reflection
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
