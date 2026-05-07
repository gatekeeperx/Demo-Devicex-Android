package com.gatekeeperx.devicex.foodhub.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Logcat-backed [AnalyticsService] for the demo app.
 *
 * In a production Rappi integration this would be swapped for a real
 * Firebase Performance / Datadog / internal metrics implementation via
 * the Hilt module — no call-site changes needed.
 *
 * Cost: negligible — [Log.d] calls are no-ops in release builds when
 * the log level is stripped by ProGuard/R8.
 */
@Singleton
class LogcatAnalyticsService @Inject constructor() : AnalyticsService {

    override fun trackTaskDuration(taskName: String, durationMs: Long) {
        Log.d(TAG, "📊 PERF | task=$taskName duration=${durationMs}ms")
    }

    override fun trackTotalStartupDuration(durationMs: Long) {
        Log.d(TAG, "📊 PERF | TOTAL startup duration=${durationMs}ms")
    }

    private companion object {
        private const val TAG = "StartupAnalytics"
    }
}
