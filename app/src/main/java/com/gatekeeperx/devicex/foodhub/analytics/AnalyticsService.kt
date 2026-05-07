package com.gatekeeperx.devicex.foodhub.analytics

/**
 * Contract for startup performance analytics.
 *
 * This is intentionally minimal — in a real Rappi-style setup this would
 * forward events to Firebase Performance / Datadog / internal metrics.
 * For the demo it is backed by [LogcatAnalyticsService].
 */
interface AnalyticsService {

    /**
     * Records the wall-clock duration of a single [StartAppTask].
     *
     * @param taskName  Simple name of the task class (e.g. "GatekeeperXStartAppTask")
     * @param durationMs Wall-clock time in milliseconds from task start to completion/failure
     */
    fun trackTaskDuration(taskName: String, durationMs: Long)

    /**
     * Records the total duration of the entire startup sequence
     * (from first task launch to all tasks joined).
     */
    fun trackTotalStartupDuration(durationMs: Long)
}
