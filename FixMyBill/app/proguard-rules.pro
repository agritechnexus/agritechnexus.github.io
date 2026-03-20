# Keep data classes for Gson
-keepclassmembers class com.fixmybill.app.data.remote.dto.** { *; }
-keepclassmembers class com.fixmybill.app.domain.model.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Room
-keep class * extends androidx.room.RoomDatabase
