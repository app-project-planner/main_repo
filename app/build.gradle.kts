plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


android {
    namespace = "com.example.mobile_pj"
    compileSdk = 35 // 최신 compileSdk 버전 34로 변경 (35는 미리보기 버전일 수 있음)

    defaultConfig {
        applicationId = "com.example.mobile_pj"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // 최신 Java 17로 설정
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17" // Kotlin도 Java 17과 호환되도록 설정
    }

    buildFeatures {
        compose = true // Compose 활성화
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Jetpack Compose 최신 안정화 버전
    }

    viewBinding.isEnabled = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Jetpack Compose 의존성 추가
    implementation(platform("androidx.compose:compose-bom:2023.10.01")) // BOM 최신화

    // Compose UI 및 Material3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // Compose Activity
    implementation("androidx.activity:activity-compose:1.8.0")

    // 테스트 의존성
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //파이어베이스 의존성 추가
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation("com.google.firebase:firebase-auth:21.0.8")

}
