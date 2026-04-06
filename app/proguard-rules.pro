# MERIDIAN ProGuard Rules
# Applied during release builds (isMinifyEnabled = true in release buildType)

# ---- MediaPipe GenAI (Gemma 4 on-device inference) ---- #
-keep class com.google.mediapipe.** { *; }
-keep class com.google.android.odml.** { *; }
-keepclassmembers class com.google.mediapipe.** { *; }
-dontwarn com.google.mediapipe.**

# ---- Room Database (LOCUS) ---- #
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.TypeConverter class * { *; }

# ---- Hilt Dependency Injection ---- #
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}
-keep @dagger.hilt.android.HiltAndroidApp class * extends android.app.Application
-keep @dagger.hilt.android.AndroidEntryPoint class *

# ---- Kotlin Serialization ---- #
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class **$$serializer {
    public static ** INSTANCE;
}

# ---- Kotlin Coroutines ---- #
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ---- CameraX (IRIS capture) ---- #
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ---- MERIDIAN core packages ---- #
-keep class ai.or4cl3.meridian.model.** { *; }
-keep class ai.or4cl3.meridian.ai.** { *; }
-keep class ai.or4cl3.meridian.data.** { *; }

# ---- Suppress common warnings ---- #
-dontwarn java.lang.invoke.StringConcatFactory
