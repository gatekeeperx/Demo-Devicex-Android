package com.gatekeeperx.devicex.foodhub.startup

import android.util.Log
import com.gatekeeperx.devicex.foodhub.analytics.AnalyticsService
import com.gatekeeperx.devicex.foodhub.di.ApplicationScope
import com.gatekeeperx.devicex.foodhub.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates all [StartAppTask] instances at app startup.
 *
 * ## Design: Parallel dispatch with per-task performance tracking
 * All tasks are launched concurrently using [CoroutineScope.launch] so that
 * slow SDKs do not block faster ones. The [MainApplicationComparator] controls
 * *dispatch order* (which coroutine is launched first), not completion order.
 *
 * Parallel execution is intentional:
 * - Firebase, DeviceX, and other SDKs have no inter-dependency.
 * - Launching them concurrently reduces total cold-start time.
 * - Performance data per-task is the key signal for identifying bottlenecks.
 *
 * ## Why [CoroutineScope] and not GlobalScope?
 * The injected [appScope] is backed by a [kotlinx.coroutines.SupervisorJob],
 * meaning a single task failure never cancels sibling tasks or the scope.
 * It is tied to the application process lifetime — effectively equivalent to
 * GlobalScope but explicitly scoped and testable.
 *
 * ## Threading
 * [initialize] returns immediately (non-blocking). All heavy work runs on
 * [ioDispatcher]. SDK tasks that require main-thread init use
 * [kotlinx.coroutines.withContext] internally.
 *
 * ## Error handling
 * Each task is wrapped in a try/catch. A failing task logs the error and
 * reports the duration to [AnalyticsService] but never crashes the app.
 *
 * @param tasks         The full set of registered [StartAppTask] implementations.
 * @param comparator    Determines launch order of the tasks.
 * @param analyticsService Reports per-task and total durations.
 * @param appScope      Application-lifetime [CoroutineScope] with SupervisorJob.
 * @param ioDispatcher  Dispatcher used for background task execution.
 */
@Singleton
class MainApplicationInitializer @Inject constructor(
    private val tasks: Set<@JvmSuppressWildcards StartAppTask>,
    private val comparator: Comparator<StartAppTask>,
    private val analyticsService: AnalyticsService,
    @param:ApplicationScope private val appScope: CoroutineScope,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    /**
     * Kicks off all startup tasks in parallel and returns immediately.
     *
     * Intended to be called from the Application class via Hilt method injection.
     * The call is non-blocking — Application.onCreate() completes instantly so
     * the OS can render the first frame (Splash screen) while SDKs load in the
     * background.
     */
    fun initialize() {
        appScope.launch(ioDispatcher) {
            val sortedTasks = tasks.sortedWith(comparator)

            Log.i(TAG, "🚀 Starting ${sortedTasks.size} startup tasks (parallel): " +
                    sortedTasks.joinToString { it::class.simpleName ?: "?" })

            // System.nanoTime() is monotonic and not subject to wall-clock adjustments,
            // giving sub-millisecond precision for startup profiling comparisons.
            val wallStartNs = System.nanoTime()

            // Launch every task in parallel. SupervisorJob ensures one failure
            // does not cancel the others.
            val jobs = sortedTasks.map { task ->
                launch {
                    val taskName = task::class.simpleName ?: "UnknownTask"
                    val taskStartNs = System.nanoTime()
                    Log.d(TAG, "  ▶ $taskName starting…")
                    try {
                        task.doWork()
                        val elapsedMs = (System.nanoTime() - taskStartNs) / 1_000_000.0
                        Log.i(TAG, "  ✅ $taskName completed in ${"%.3f".format(elapsedMs)}ms")
                        analyticsService.trackTaskDuration(taskName, elapsedMs.toLong())
                    } catch (e: Exception) {
                        val elapsedMs = (System.nanoTime() - taskStartNs) / 1_000_000.0
                        Log.e(TAG, "  ❌ $taskName FAILED after ${"%.3f".format(elapsedMs)}ms — ${e.message}", e)
                        analyticsService.trackTaskDuration("${taskName}_ERROR", elapsedMs.toLong())
                    }
                }
            }

            // Wait for every task to finish before reporting total time.
            jobs.joinAll()

            val totalElapsedMs = (System.nanoTime() - wallStartNs) / 1_000_000.0
            Log.i(TAG, "🏁 All startup tasks finished. Total wall-clock: ${"%.3f".format(totalElapsedMs)}ms")
            analyticsService.trackTotalStartupDuration(totalElapsedMs.toLong())
        }
    }

    private companion object {
        private const val TAG = "AppInitializer"
    }
}
