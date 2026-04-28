plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.gigmap_frontend_sprint1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gigmap_frontend_sprint1"
        minSdk = 24
        targetSdk = 36
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
        compose = true
    }
}

dependencies {
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    //librerías para retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")// build.gradle.kts
    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.0")
    implementation("androidx.compose.animation:animation:1.6.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.geometry)

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.json:json:20231013")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.cloudinary:cloudinary-android:2.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    //google maps
    implementation("com.google.maps.android:maps-compose:5.0.0") // Use the latest version
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.0")
    implementation(libs.play.services.places)
    implementation(libs.androidx.espresso.core)
    // librerías para
    val nav_version = "2.9.5"
    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.animation:animation:1.6.0")
    implementation("androidx.compose.foundation:foundation:1.7.0")
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("com.exyte:animated-navigation-bar:1.0.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}