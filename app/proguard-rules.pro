# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }

# Keep Kotlin classes
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# Keep your app's classes
-keep class com.example.mytimetablemaker.** { *; }
-keep class com.mytimetablemaker.** { *; }

# Keep ViewBinding classes
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(android.view.LayoutInflater);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
    public static *** bind(android.view.View);
}

# Keep data binding classes
-keep class * extends androidx.databinding.ViewDataBinding {
    public static *** inflate(android.view.LayoutInflater);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
    public static *** bind(android.view.View);
}

# Security: Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Security: Obfuscate sensitive data
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Security: Remove debug information
-renamesourcefileattribute SourceFile 