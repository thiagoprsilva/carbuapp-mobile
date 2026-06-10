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

# ----------------------------------------------------------------------
# Gson + Retrofit: os DTOs (Request/Response/Dto) são serializados via
# reflection pelo NOME dos campos. Sem essas regras, o R8 ofusca os
# campos (ex: "email" -> "a", "senha" -> "b") e o backend recebe um JSON
# com chaves erradas, retornando "email e senha são obrigatórios." mesmo
# com os dados corretos.
# ----------------------------------------------------------------------
-keep class br.com.carbuapp.**.*Dto { <fields>; }
-keep class br.com.carbuapp.**.*Request { <fields>; }
-keep class br.com.carbuapp.**.*Response { <fields>; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}