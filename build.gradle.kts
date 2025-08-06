// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
}
buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        val navVersion = "2.7.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}
