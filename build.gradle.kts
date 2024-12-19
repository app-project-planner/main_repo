// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Firebase Gradle 플러그인 추가
        classpath("com.google.gms:google-services:4.3.15") // 최신 버전 확인 가능
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10") // Kotlin 플러그인 명시적으로 추가
    }
}
