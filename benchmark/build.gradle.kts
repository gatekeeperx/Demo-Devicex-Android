/**
 * :benchmark — Macrobenchmark module for the FoodHub demo app.
 *
 * This is a `com.android.test` module: the entire source set is test code
 * that runs as a separate instrumentation APK driving the :app process.
 *
 * ## How to run
 *
 *   ./gradlew :benchmark:connectedBenchmarkAndroidTest
 *
 * Or from Android Studio:
 *   Run → Edit Configurations → select the benchmark test class and the
 *   "benchmark" build variant in the benchmark module.
 *
 * ## Output
 * Results are written to:
 *   benchmark/build/outputs/connected_android_test_additional_output/
 * Open the JSON file in Android Studio → Run → Import Benchmark Results.
 *
 * ## What is measured
 *   - timeToInitialDisplayMs  (TTID) — first frame rendered
 *   - timeToFullyDrawnMs      (TTFD) — reportFullyDrawn() called in MainActivity
 *   - DeviceX#configure       (ms)   — Trace section wrapping Devicex.configure()
 *
 * ## Target build type
 * The benchmark targets the ":app" module's "benchmarkable" build type:
 *   - isDebuggable = false → JIT + AOT active, accurate perf numbers
 *   - isMinifyEnabled = false → class names visible in Perfetto traces
 */
plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.gatekeeperx.devicex.benchmark"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,LOW-BATTERY"
    }

    buildTypes {
        /**
         * "benchmark" build type for this test module.
         *
         * Must be non-debuggable to match the "benchmarkable" variant in :app.
         * signingConfig = debug → allows easy device install without a release keystore.
         * matchingFallbacks → if a transitive dependency lacks "benchmark", use "release".
         */
        create("benchmark") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    /**
     * Links this test module to the :app module.
     * AGP builds :app in "benchmarkable" and this module in "benchmark" when running tests.
     */
    targetProjectPath = ":app"

    /**
     * Required for the benchmark APK to run as a separate process that instruments :app,
     * rather than running inside :app's process.
     */
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

/**
 * Restrict this module to only build the "benchmark" variant.
 * Prevents accidental "debug" builds of a test-only module.
 */
androidComponents {
    beforeVariants(selector().all()) { variantBuilder ->
        variantBuilder.enable = variantBuilder.buildType == "benchmark"
    }
}

dependencies {
    implementation(libs.junit)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro)
}
