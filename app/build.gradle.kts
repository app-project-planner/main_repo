plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.mobile_pj"
    compileSdk = 34 // 최신 compileSdk 버전 34로 변경 (35는 미리보기 버전일 수 있음)

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
    implementation(libs.firebase.firestore.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Compose Activity
    implementation("androidx.activity:activity-compose:1.8.0")

    // Google Play services
    implementation(libs.google.services)
    implementation(libs.firebase.auth)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.auth)

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // 테스트 의존성
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
