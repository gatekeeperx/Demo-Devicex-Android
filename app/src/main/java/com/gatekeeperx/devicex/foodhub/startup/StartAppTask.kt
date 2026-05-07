package com.gatekeeperx.devicex.foodhub.startup

/**
 * Contract for a single unit of application startup work.
 *
 * Each SDK or library that needs to be initialized at boot should provide
 * one implementation of this interface and bind it into the Hilt multibinding
 * set via [dagger.multibindings.IntoSet].
 *
 * ## Threading
 * [doWork] is a suspend function and will be launched from an IO-dispatcher
 * coroutine by [MainApplicationInitializer]. Implementations may switch
 * to [kotlinx.coroutines.Dispatchers.Main] internally if the SDK requires
 * main-thread initialization.
 *
 * ## Error handling
 * Implementations must NOT swallow exceptions — propagate them so the
 * [MainApplicationInitializer] can log the failure and report the duration.
 * The orchestrator uses a [kotlinx.coroutines.SupervisorJob], so one task's
 * failure will never cancel sibling tasks.
 */
interface StartAppTask {

    /**
     * Perform the initialization work for this task.
     * Called once per app launch inside a non-main-thread coroutine.
     */
    suspend fun doWork()

    /**
     * Release any resources held by this task.
     * Called when the application is being torn down or during testing teardown.
     */
    fun clear()
}
