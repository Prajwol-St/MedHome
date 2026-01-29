plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.medhomeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.medhomeapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "GROQ_API_KEY",
            "\"${project.properties["GROQ_API_KEY"]}\""
        )
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
        buildConfig = true
    }
}

dependencies {

    // ========== CORE ==========
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ========== COMPOSE ==========
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.material:material-icons-extended")

    // ========== IMAGE LOADING ==========
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt.coil3:coil:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    // ========== MATERIAL ==========
    implementation("com.google.android.material:material:1.11.0")

    // ========== COROUTINES ==========
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ========== WORK MANAGER ==========
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // ========== LIFECYCLE ==========
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")

    // ========== FIREBASE ==========
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // ========== GOOGLE AUTH ==========
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // ========== NETWORK ==========
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // ========== CAMERA X ==========
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // ========== BARCODE ==========
    implementation(libs.zxing.android.embedded)
    implementation(libs.play.services.mlkit.barcode.scanning)

    // ========== CLOUDINARY ==========
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ========== AI ==========
    implementation(libs.generativeai)

    // ========== ANIMATIONS ==========
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // ========== UTIL ==========
    implementation(libs.guava)

    // ========== TESTING ==========
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // ========== DEBUG ==========
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
