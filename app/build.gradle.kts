plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.skycast"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skycast"
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("com.airbnb.android:lottie-compose:6.0.0")
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.compose.ui:ui:1.6.0")
    implementation ("androidx.compose.material:material:1.6.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.6.0")

    //for Kotlin + workManager
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    //Gson
    implementation ("com.google.code.gson:gson:2.8.5")
    //Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
//Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
//Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
// Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")
// optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
//Glide
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
//LiveData & Compose
    val compose_version = "1.0.0"
    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation("com.exyte:animated-navigation-bar:1.0.0")
    implementation ("com.google.android.gms:play-services-location:21.1.0")


}
