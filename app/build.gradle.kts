plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Keep these plugins from the hotfix branch
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.0.21"
    // Keep the safe-args plugin from your feature/home branch
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.example.novelonline"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.novelonline"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core and testing dependencies (common to both)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Your UI dependencies from feature/home (using newer versions)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Firebase dependencies from hotfix
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.common.ktx)
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
}