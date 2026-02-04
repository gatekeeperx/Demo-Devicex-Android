package com.gatekeeperx.devicex.foodhub

import android.app.Application
import android.util.Log
import com.gatekeeperx.android.devicex.Devicex
import dagger.hilt.android.HiltAndroidApp

/**
 * com.gatekeeperx.devicex.foodhub
 * Application class for Device Intelligence SDK initialization.
 *
 * This class initializes the Devicex SDK once at app startup.
 */
@HiltAndroidApp
class DemoApp : Application() {
    private companion object {
        private const val TAG = "App"
    }
    override fun onCreate() {
        super.onCreate()

        // Initialize Devicex SDK
        initDevicex()
    }

    private fun initDevicex() {
        try {
            Devicex.configure(this) {
                apiKey = "API_KEY"
                tenant = "TENANT"
                sandbox()
            }


        } catch (e: Exception) {

        }
    }


}
