plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.univ.doraboda"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.univ.doraboda"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dataBinding {
        enable = true
    }
}

dependencies {
    implementation("androidx.activity:activity:1.10.1")
    val room_version = "2.6.1"
    val viewModels_version1 = "1.10.1"
    val viewModels_version2 = "1.8.6"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("com.jakewharton.timber:timber:5.0.1")
    implementation ("com.google.android.material:material:1.9.0")

    //room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    //coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    //viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    //by viewModels
    implementation ("androidx.activity:activity-ktx:${viewModels_version1}")
    implementation ("androidx.fragment:fragment-ktx:${viewModels_version2}")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //recyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    //navigation
    val nav_version = "2.9.4"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
}