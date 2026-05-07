package com.gatekeeperx.devicex.foodhub.startup.tasks

import android.content.Context
import android.os.Trace
import android.util.Log
import com.gatekeeperx.android.devicex.Devicex
import com.gatekeeperx.devicex.foodhub.startup.DeviceXProvider
import com.gatekeeperx.devicex.foodhub.startup.DeviceXState
import com.gatekeeperx.devicex.foodhub.startup.EnvironmentData
import com.gatekeeperx.devicex.foodhub.startup.StartAppTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Initializes the GatekeeperX DeviceX SDK as part of the parallel startup pipeline.
 *
 * ## Why main-thread dispatch?
 * [Devicex.configure] must be called on the main thread (it registers ActivityLifecycleCallbacks
 * and accesses Android framework components that are not thread-safe). We use
 * [withContext(Dispatchers.Main)] to hop to the main thread without blocking it —
 * the calling IO coroutine suspends while the main thread processes the continuation
 * at its earliest opportunity.
 *
 * ## State management
 * [DeviceXProvider] is updated via [DeviceXState] to allow observers (ViewModels,
 * composables) to react to SDK readiness without polling or direct coupling.
 *
 * ## Performance
 * - CPU: one-time SDK init (crypto setup, device ID generation) — typically 50-300ms
 * - Memory: DeviceX SDK singleton, negligible impact on the host app heap
 * - Battery: no background work initiated here
 *
 * ## Cancellation
 * [withContext(Dispatchers.Main)] is a suspend point and will propagate cancellation
 * if the parent coroutine scope is cancelled before the main-thread hop completes.
 *
 * ## Benchmarking
 * The trace section "DeviceX#configure" wraps [Devicex.configure] on the main thread.
 * It is captured by:
 *   - Android Studio CPU Profiler → System Trace
 *   - `adb shell perfetto` traces
 *   - `TraceSectionMetric` in the Macrobenchmark module (:benchmark)
 *
 * [System.nanoTime] is used for sub-millisecond precision logging.
 *
 * @param context         Application context — safe for the SDK singleton lifetime.
 * @param environmentData SDK configuration (API key, tenant, sandbox flag).
 * @param deviceXProvider Observable holder updated with init result.
 */
class GatekeeperXStartAppTask @Inject constructor(
    @ApplicationContext private val context: Context,
    private val environmentData: EnvironmentData,
    private val deviceXProvider: DeviceXProvider,
) : StartAppTask {

    override suspend fun doWork() {
        Log.d(TAG, "Configuring DeviceX SDK (tenant=${environmentData.tenant})…")
        deviceXProvider.setState(DeviceXState.Initializing)

        // High-resolution start timestamp — nanoTime() is monotonic and not subject
        // to wall-clock adjustments (NTP, DST), making it ideal for elapsed-time measurement.
        val startNs = System.nanoTime()

        // Devicex.configure requires main-thread execution. withContext suspends
        // the current IO coroutine and resumes on the main thread without blocking it.
        //
        // Trace section "DeviceX#configure":
        //   - begin/end are both called from the main thread (required by android.os.Trace).
        //   - This section appears in Perfetto, Systrace, and the :benchmark TraceSectionMetric.
        withContext(Dispatchers.Main) {
            Trace.beginSection(TRACE_CONFIGURE)
            try {
                Devicex.configure(context) {
                    apiKey = environmentData.apiKey
                    tenant = environmentData.tenant
                    if (environmentData.isSandbox) sandbox()
                }
            } finally {
                // endSection() in finally guarantees the trace is always closed,
                // even if configure() throws unexpectedly.
                Trace.endSection()
            }
        }

        val elapsedMs = (System.nanoTime() - startNs) / 1_000_000.0
        deviceXProvider.setState(DeviceXState.Ready)
        Log.i(TAG, "DeviceX SDK configured successfully in ${"%.3f".format(elapsedMs)}ms")
    }

    override fun clear() {
        deviceXProvider.setState(DeviceXState.Initializing)
    }

    internal companion object {
        private const val TAG = "GatekeeperXStartAppTask"

        /**
         * Perfetto / Systrace / Macrobenchmark trace section name.
         * Must match `TraceSectionMetric` argument in :benchmark module exactly.
         */
        const val TRACE_CONFIGURE = "DeviceX#configure"
    }
}
