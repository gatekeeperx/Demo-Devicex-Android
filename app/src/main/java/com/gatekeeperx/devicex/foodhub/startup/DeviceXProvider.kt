package com.gatekeeperx.devicex.foodhub.startup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observable holder for the DeviceX SDK readiness state.
 *
 * Any part of the application (ViewModels, composables, Activities) can
 * observe [state] as a [StateFlow] to react when the SDK becomes ready
 * without coupling to the startup infrastructure.
 *
 * ## Why StateFlow and not a callback?
 * StateFlow is lifecycle-safe, supports multiple collectors, and replays
 * the last value to late subscribers — essential for fragments/activities
 * that might start observing after the SDK has already initialised.
 */
@Singleton
class DeviceXProvider @Inject constructor() {

    private val _state = MutableStateFlow<DeviceXState>(DeviceXState.Initializing)

    /** Publicly exposed read-only state. */
    val state: StateFlow<DeviceXState> = _state.asStateFlow()

    /** Returns true only when DeviceX has been successfully configured. */
    val isReady: Boolean get() = _state.value is DeviceXState.Ready

    /** Called exclusively by [com.gatekeeperx.devicex.foodhub.startup.tasks.GatekeeperXStartAppTask]. */
    internal fun setState(newState: DeviceXState) {
        _state.value = newState
    }
}

/**
 * Sealed hierarchy representing the lifecycle of DeviceX SDK initialisation.
 */
sealed interface DeviceXState {
    /** SDK configuration has not yet completed. */
    data object Initializing : DeviceXState

    /** SDK configured successfully — safe to call DeviceX APIs. */
    data object Ready : DeviceXState

    /** SDK configuration failed. [cause] contains the original exception. */
    data class Failed(val cause: Throwable) : DeviceXState
}
