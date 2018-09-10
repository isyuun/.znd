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
#useLibrary 'org.apache.http.legacy'
-keep class org.apache.http.** { *; }
-keepclassmembers class org.apache.http.** {*;}
-dontwarn org.apache.**
-keep class android.net.http.** { *; }
-keepclassmembers class android.net.http.** {*;}
-dontwarn android.net.**

#javazoom.jl.decoder
-dontwarn javazoom.jl.**
-keep class javazoom.jl.** { *; }
-keep interface javazoom.jl.** { *; }

#FileExplorer (com.lamerman.*)
-dontwarn com.lamerman.**
-keep class com.lamerman.** { *; }
-keep interface com.lamerman.** { *; }

##android.support.v4
#-dontwarn android.support.**
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }
#
##android.support.v7
#-dontwarn android.support.v7.**
#-keep class android.support.v7.** { *; }
#-keep interface android.support.v7.** { *; }
