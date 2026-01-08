import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

val localProperties = Properties()
val file = project.rootProject.file("local.properties")
if(file.exists()) file.inputStream().use { localProperties.load(it) }

android {
    namespace = "com.univ.doraboda"
    compileSdk = 36
    ndkVersion = "29.0.14206865"

    defaultConfig {
        applicationId = "com.univ.doraboda"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "apiKey", localProperties["api_key"] as String)
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
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
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
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    implementation ("com.jakewharton.timber:timber:5.0.1")
    implementation ("com.google.android.material:material:1.9.0")

    //room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    //viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    //by viewModels
    implementation ("androidx.activity:activity-ktx:${viewModels_version1}")
    implementation ("androidx.fragment:fragment-ktx:${viewModels_version2}")

    //glide
    implementation ("com.github.bumptech.glide:glide:5.0.5")

    //recyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    //navigation
    val nav_version = "2.9.4"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    val media3_version = "1.8.0"

    //media session
    implementation("androidx.media3:media3-session:$media3_version")

    //Exoplayer
    implementation("androidx.media3:media3-exoplayer:$media3_version")
    implementation("androidx.media3:media3-ui:$media3_version")
    implementation("androidx.media3:media3-common:$media3_version")

    //hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")

    //guava
    implementation("com.google.guava:guava:33.5.0-android")
    implementation("androidx.concurrent:concurrent-futures:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.10.2")

    //mpChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")

    //datastore
    implementation("androidx.datastore:datastore:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    //gson
    implementation("com.google.code.gson:gson:2.13.2")
}

kapt {
    correctErrorTypes = true
}