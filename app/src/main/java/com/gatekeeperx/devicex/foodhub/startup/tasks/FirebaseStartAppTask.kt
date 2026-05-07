package com.gatekeeperx.devicex.foodhub.startup.tasks

import android.util.Log
import com.gatekeeperx.devicex.foodhub.startup.StartAppTask
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock Firebase Analytics / Crashlytics initialization task.
 *
 * In a real app this would call:
 * ```
 * FirebaseApp.initializeApp(context)
 * FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
 * ```
 *
 * For the demo, a [delay] simulates the typical Firebase init time (~100-200ms)
 * so we can observe realistic parallel startup behaviour and compare it against
 * the DeviceX SDK init time in Logcat.
 *
 * ## Performance
 * - CPU: negligible — Firebase init is mostly one-time reflection + IO
 * - Memory: negligible beyond the Firebase library itself
 * - Battery: none at this stage
 *
 * ## Cancellation
 * The [delay] call is a suspension point and will be cancelled if the parent
 * coroutine scope is cancelled.
 */
class FirebaseStartAppTask @Inject constructor() : StartAppTask {

    override suspend fun doWork() {
        Log.d(TAG, "Initializing Firebase (mock)…")
        // Simulate Firebase cold-start latency
        delay(MOCK_FIREBASE_INIT_MS)
        Log.d(TAG, "Firebase initialized (mock)")
    }

    override fun clear() {
        // Firebase has no teardown in this mock
    }

    private companion object {
        private const val TAG = "FirebaseStartAppTask"
        private const val MOCK_FIREBASE_INIT_MS = 120L
    }
}
