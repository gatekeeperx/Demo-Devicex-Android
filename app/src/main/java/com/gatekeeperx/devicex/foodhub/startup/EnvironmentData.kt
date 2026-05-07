package com.gatekeeperx.devicex.foodhub.startup

/**
 * Holds runtime configuration required for SDK initialization.
 *
 * In a production environment these values would come from a secure
 * backend-provisioned config (e.g. Remote Config, BuildConfig, or
 * an encrypted config file) — never hardcoded in the AAR.
 *
 * For this demo app the values are provided as constructor parameters
 * and supplied from the Hilt [com.gatekeeperx.devicex.foodhub.di.AppModule].
 */
data class EnvironmentData(
    /** GatekeeperX / DeviceX API key. */
    val apiKey: String,
    /** Tenant identifier used by the DeviceX backend. */
    val tenant: String,
    /** Whether to use the sandbox (non-production) environment. */
    val isSandbox: Boolean = true,
)
