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
-keep class com.kakao.** { *; }
-keepattributes Signature
-keepclassmembers class * {
  public static <fields>;
  public *;
}
-dontwarn android.support.v4.**,com.ning.http.client.**,org.jboss.netty.**, org.slf4j.**, com.fasterxml.jackson.databind.**, com.google.android.gms.**
-keep class android.support.v8.renderscript.** { *; }

###...빳따...####
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keep class **$$ViewBinder { *; }

###...노답...####
-ignorewarnings
#-keep class * {
#    public private *;
#}
#-dontwarn kr.keumyoung.mukin.**
#-keep class kr.keumyoung.mukin.** { *; }
#-keep interface kr.keumyoung.mukin.** { *; }

###...kr.keumyoung.mukin.api...###
-dontwarn kr.keumyoung.mukin.api.**
-keep class kr.keumyoung.mukin.api.** { *; }
-keep interface kr.keumyoung.mukin.api.** { *; }

###...kr.keumyoung.mukin.data.request...###
-dontwarn kr.keumyoung.mukin.data.request.**
-keep class kr.keumyoung.mukin.data.request.** { *; }
-keep interface kr.keumyoung.mukin.data.request.** { *; }

##...JNI...###
-keepclasseswithmembernames class * { native <methods>; }

###...kr.keumyoung.mukin.util...###
-dontwarn kr.keumyoung.mukin.util.**
-keep class kr.keumyoung.mukin.util.** { *; }
-keep interface kr.keumyoung.mukin.util.** { *; }

###...kr.keumyoung.mukin.helper...###
-dontwarn kr.keumyoung.mukin.helper.**
-keep class kr.keumyoung.mukin.helper.** { *; }
-keep interface kr.keumyoung.mukin.helper.** { *; }

###...kr.keumyoung.mukin.util.AudioJNI...###
-dontwarn kr.keumyoung.mukin.util.AudioJNI.**
-keep class kr.keumyoung.mukin.util.AudioJNI.** { *; }
-keep interface kr.keumyoung.mukin.util.AudioJNI.** { *; }

###...kr.keumyoung.mukin.util.PlayerJNI...###
-dontwarn kr.keumyoung.mukin.util.PlayerJNI.**
-keep class kr.keumyoung.mukin.util.PlayerJNI.** { *; }
-keep interface kr.keumyoung.mukin.util.PlayerJNI.** { *; }

###...kr.keumyoung.mukin.util.PlayerKyUnpackJNI...###
-dontwarn kr.keumyoung.mukin.util.PlayerKyUnpackJNI.**
-keep class kr.keumyoung.mukin.util.PlayerKyUnpackJNI.** { *; }
-keep interface kr.keumyoung.mukin.util.PlayerKyUnpackJNI.** { *; }
