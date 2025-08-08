plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.google.gms.services)
}

android {
    namespace = "com.example.novelonline"
    // Changed to 34 to resolve the AGP warning. This is recommended.
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.novelonline"
        minSdk = 24
        // Changed to 34 to match compileSdk
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.glide)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Firebase dependencies
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Add the dependencies for Firebase products you want to use
    // Versions are managed by the BoM, so they are not specified here
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    // This is for fragment management, not a Firebase library
    implementation(libs.androidx.fragment)

    // REMOVED implementation(libs.firebase.common.ktx) because it's handled by the BoM
}
