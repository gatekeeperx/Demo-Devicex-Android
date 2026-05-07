package com.gatekeeperx.devicex.foodhub

import android.app.Application
import com.gatekeeperx.devicex.foodhub.startup.MainApplicationInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point for the FoodHub demo app.
 *
 * ## Startup architecture (Rappi pattern)
 *
 * Hilt processes `@HiltAndroidApp` and generates a base class that calls
 * `component.inject(this)` at the top of [onCreate]. This injection step:
 *
 * 1. Resolves all `@Inject`-annotated fields (none here).
 * 2. Calls all `@Inject`-annotated *methods* immediately after construction —
 *    this is Dagger's **Method Injection** pattern.
 *
 * Because [mainStartupTasks] is `@Inject`-annotated, Hilt invokes it
 * automatically as part of the injection phase, before any of the custom
 * code inside [onCreate] runs.
 *
 * ## Why method injection instead of field injection?
 * - It expresses intent: "this method is the startup trigger", not just
 *   a lazy dependency holder.
 * - It mirrors exactly how Rappi triggers the initializer from the Application
 *   class without the caller needing to hold a reference to the initializer.
 *
 * ## Non-blocking guarantee
 * [MainApplicationInitializer.initialize] fires a coroutine on
 * `Dispatchers.IO` and returns immediately. [onCreate] completes in
 * microseconds, allowing the OS to render the first frame (Splash / launch
 * screen) while SDKs load in the background.
 */
@HiltAndroidApp
class DemoApp : Application() {

    /**
     * Method Injection entry point for the startup task pipeline.
     *
     * Hilt calls this method during the `component.inject(this)` phase
     * that the generated `Hilt_DemoApp` base class triggers inside [onCreate].
     * The call happens before any code in the body of [onCreate].
     *
     * [MainApplicationInitializer.initialize] is non-blocking — it launches
     * coroutines and returns immediately, so there is zero impact on the main
     * thread or app cold-start time.
     */
    @Inject
    internal fun mainStartupTasks(initializer: MainApplicationInitializer) {
        initializer.initialize()
    }

    // onCreate() is intentionally omitted.
    // Hilt_DemoApp (the generated base class) overrides onCreate() and calls
    // component.inject(this), which triggers @Inject-annotated methods — including
    // mainStartupTasks() above — before the rest of onCreate() runs.
    // No further SDK init is needed here; everything is handled by the
    // StartAppTask pipeline registered in StartupTasksModule.
}
