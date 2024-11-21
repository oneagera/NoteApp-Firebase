plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.markus.noteapp_firebase"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.markus.noteapp_firebase"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose dependencies
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.navigation.compose)
    //optional - extended material icons not included in androidx.compose.material:material-icons-core
    //which is included in **implementation("androidx.compose.material3:material3") dependency
    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.hilt.navigation.compose)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    //authentication
    implementation(libs.firebase.auth.ktx)
    //firestore
    implementation(libs.firebase.firestore.ktx)
    // coroutine
    implementation(libs.kotlinx.coroutines.play.services)

    //dagger hilt dependency
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp (libs.androidx.hilt.compiler)

    // Coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)
}