package com.gatekeeperx.devicex.foodhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.gatekeeperx.devicex.foodhub.ui.food.home.FoodDeliveryScreen
import com.gatekeeperx.devicex.foodhub.ui.food.login.LoginScreen
import com.gatekeeperx.devicex.foodhub.ui.food.splash.SplashScreen
import com.gatekeeperx.devicex.foodhub.ui.theme.FoodDeliveryTheme
import com.gatekeeperx.devicex.foodhub.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity with Splash → Login → Home flow.
 *
 * ## TTFD (Time to Fully Drawn)
 * [reportFullyDrawn] is called when the Login screen is first shown — the earliest
 * point at which the app is interactive and the SDK startup pipeline has had
 * sufficient time to complete (the 2.5s splash absorbs most SDK init overhead).
 *
 * This signal is consumed by:
 *   - Android vitals (Play Console)
 *   - Macrobenchmark's `StartupTimingMetric` → `timeToFullyDrawnMs`
 *   - `adb shell am start -W`
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodDeliveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    private fun AppNavigation() {
        var currentScreen by remember { mutableStateOf(Screen.Splash) }

        // Signal TTFD the first time the Login screen is displayed.
        // reportFullyDrawn() is idempotent — safe to re-call on recomposition.
        LaunchedEffect(currentScreen) {
            if (currentScreen == Screen.Login) {
                reportFullyDrawn()
            }
        }

        // Location permission launcher
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->
            // Permissions handled, continue to home screen
            currentScreen = Screen.Home
        }

        when (currentScreen) {
            Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = Screen.Login
                    }
                )
            }

            Screen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        // Request location permissions after successful login
                        if (!PermissionUtils.hasLocationPermission(this@MainActivity)) {
                            locationPermissionLauncher.launch(
                                PermissionUtils.getLocationPermissions()
                            )
                        } else {
                            currentScreen = Screen.Home
                        }
                    }
                )
            }

            Screen.Home -> {
                FoodDeliveryScreen(
                    onDishClick = { dishId ->
                        startActivity(DishDetailsActivity.createIntent(this, dishId))
                    },
                    onCartClick = {
                        startActivity(CheckoutCartActivity.createIntent(this))
                    }
                )
            }
        }
    }

    private enum class Screen {
        Splash,
        Login,
        Home
    }
}
