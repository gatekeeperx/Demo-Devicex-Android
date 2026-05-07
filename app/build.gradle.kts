plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.gatekeeperx.devicex.foodhub"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.gatekeeperx.devicex.foodhub"
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

        /**
         * "benchmarkable" build type — mirrors release but signed with the debug key
         * so the Macrobenchmark APK can be installed without a release keystore.
         *
         * Rules:
         *   - isDebuggable = false  → required for accurate profiling (JIT + AOT enabled)
         *   - isMinifyEnabled = false → keep class/method names visible in Perfetto traces
         *   - matchingFallbacks → any library without a "benchmarkable" variant falls back to "release"
         *
         * To run: ./gradlew :benchmark:connectedBenchmarkAndroidTest
         */
        create("benchmarkable") {
            isDebuggable = false
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Google Material
    implementation(libs.google.material)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // GatekeeperX SDK
    implementation(libs.gatekeeperx.devicex)

    // ProfileInstaller — enables Macrobenchmark to install a baseline profile
    // at benchmark time, giving accurate AOT-compiled startup measurements.
    implementation(libs.androidx.profileinstaller)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Image Loading
    implementation(libs.coil.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}