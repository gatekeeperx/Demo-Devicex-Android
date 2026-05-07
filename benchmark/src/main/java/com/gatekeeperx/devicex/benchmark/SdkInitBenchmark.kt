package com.gatekeeperx.devicex.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Macrobenchmark for the GatekeeperX DeviceX SDK initialization.
 *
 * ## Purpose
 * Provides reproducible, multi-iteration measurements to compare SDK initialization
 * cost **before and after** implementation changes or SDK version upgrades.
 *
 * ## Metrics captured
 * | Metric                        | What it measures                                          |
 * |-------------------------------|-----------------------------------------------------------|
 * | `timeToInitialDisplayMs`      | TTID: first frame rendered by the OS                      |
 * | `timeToFullyDrawnMs`          | TTFD: `reportFullyDrawn()` call when Login screen appears |
 * | `DeviceX#configure_sumMs`     | Total time inside `Devicex.configure()` on the main thread|
 *
 * ## How to run
 *
 *   # One-liner from terminal (device/emulator must be connected):
 *   ./gradlew :benchmark:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=com.gatekeeperx.devicex.benchmark.SdkInitBenchmark
 *
 *   # Or run individual test methods from Android Studio by switching the
 *   # :benchmark module to the "benchmark" build variant.
 *
 * ## Before / After comparison workflow
 *
 *   1. Run [coldStartBaseline] before any change → save JSON report as "before.json"
 *   2. Make your SDK/code change
 *   3. Run [coldStartBaseline] again → save as "after.json"
 *   4. In Android Studio: Run → Benchmark → Import Results → select both files
 *      to see a side-by-side diff with confidence intervals.
 *
 * ## Interpreting results
 * Results are emitted to Logcat (tag "Benchmark") and to:
 *   benchmark/build/outputs/connected_android_test_additional_output/<device>/
 *
 * A reduction in `DeviceX#configure_sumMs` directly measures SDK init speedup.
 * A reduction in `timeToFullyDrawnMs` measures end-to-end startup improvement.
 *
 * ## Important: run on a physical device
 * Emulators produce inflated and variable results. Use a physical device for
 * numbers that represent real-world user experience.
 */
@RunWith(AndroidJUnit4::class)
class SdkInitBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Measures cold-start performance with no pre-compiled code (worst case).
     *
     * [CompilationMode.None] simulates a fresh install where no AOT compilation
     * has happened yet. This is the most conservative baseline.
     *
     * Use this test to answer: "how long does SDK init take on a fresh install?"
     */
    @Test
    fun coldStartBaseline() = benchmarkRule.measureRepeated(
        packageName = APP_PACKAGE,
        metrics = listOf(
            StartupTimingMetric(),
            TraceSectionMetric(TRACE_SDK_CONFIGURE),
        ),
        compilationMode = CompilationMode.None(),
        iterations = ITERATIONS,
        startupMode = StartupMode.COLD,
        setupBlock = {
            // Kill the app process and return to home screen before each iteration.
            // This guarantees a true cold start (Application.onCreate() re-runs).
            pressHome()
        },
    ) {
        // Launch the app and wait for the first frame to be rendered (TTID).
        startActivityAndWait()

        // Wait for the Login screen to appear — signals that:
        //   1. The 2.5s splash animation completed.
        //   2. The SDK startup pipeline (GatekeeperXStartAppTask) has finished.
        //   3. reportFullyDrawn() has been called (→ timeToFullyDrawnMs).
        //
        // Timeout: 10s. If the app does not reach Login within 10s the test fails,
        // which surfaces regressions where startup hangs or crashes.
        device.wait(Until.hasObject(By.text(LOGIN_BUTTON_TEXT)), LOGIN_SCREEN_TIMEOUT_MS)
    }

    /**
     * Measures cold-start performance with full AOT compilation (best case / production).
     *
     * [CompilationMode.Full] simulates a device that has run `adb shell cmd package compile`
     * or has installed a baseline profile. This represents the optimal scenario.
     *
     * Compare this against [coldStartBaseline] to understand the benefit of
     * baseline profiles for SDK init.
     */
    @Test
    fun coldStartFullyCompiled() = benchmarkRule.measureRepeated(
        packageName = APP_PACKAGE,
        metrics = listOf(
            StartupTimingMetric(),
            TraceSectionMetric(TRACE_SDK_CONFIGURE),
        ),
        compilationMode = CompilationMode.Full(),
        iterations = ITERATIONS,
        startupMode = StartupMode.COLD,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
        device.wait(Until.hasObject(By.text(LOGIN_BUTTON_TEXT)), LOGIN_SCREEN_TIMEOUT_MS)
    }

    /**
     * Measures warm-start performance (process alive, activity recreated).
     *
     * [StartupMode.WARM] keeps the app process alive between iterations but
     * recreates the Activity. The SDK init does NOT re-run (it's a singleton),
     * so [TRACE_SDK_CONFIGURE] will show ~0ms. This test is useful to verify
     * that warm-start is not regressed by the SDK singleton holding resources.
     */
    @Test
    fun warmStartOverhead() = benchmarkRule.measureRepeated(
        packageName = APP_PACKAGE,
        metrics = listOf(
            StartupTimingMetric(),
            TraceSectionMetric(TRACE_SDK_CONFIGURE),
        ),
        compilationMode = CompilationMode.None(),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
        device.wait(Until.hasObject(By.text(LOGIN_BUTTON_TEXT)), LOGIN_SCREEN_TIMEOUT_MS)
    }

    private companion object {
        /** Package name of the :app module. Must match app/build.gradle.kts namespace. */
        const val APP_PACKAGE = "com.gatekeeperx.devicex.foodhub"

        /**
         * Trace section name — must match [GatekeeperXStartAppTask.TRACE_CONFIGURE] exactly.
         * The Macrobenchmark framework captures the sum of all occurrences across the run.
         */
        const val TRACE_SDK_CONFIGURE = "DeviceX#configure"

        /** Text of the Sign In button on the Login screen — used as the "fully drawn" signal. */
        const val LOGIN_BUTTON_TEXT = "Sign In"

        /** Timeout waiting for the Login screen after app launch. */
        const val LOGIN_SCREEN_TIMEOUT_MS = 10_000L

        /**
         * Number of iterations per test.
         *
         * 5 iterations is the minimum for statistically meaningful results.
         * Increase to 10 for CI gating or when results show high variance (stddev > 10% of mean).
         *
         * Cost: each cold-start iteration ≈ 3-4s (2.5s splash + SDK init + TTID overhead).
         * Total per test at 5 iterations ≈ 20-25s on device.
         */
        const val ITERATIONS = 5
    }
}
