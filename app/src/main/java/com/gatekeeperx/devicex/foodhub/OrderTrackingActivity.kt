package com.gatekeeperx.devicex.foodhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gatekeeperx.devicex.foodhub.ui.food.order.OrderScreen
import com.gatekeeperx.devicex.foodhub.ui.theme.FoodDeliveryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that displays order tracking with 10-second delivery simulation
 */
@AndroidEntryPoint
class OrderTrackingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodDeliveryTheme {
                OrderScreen(
                    onOrderCompleted = {
                        // Navigate back to MainActivity (Home)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        /**
         * Create intent to launch OrderTrackingActivity
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, OrderTrackingActivity::class.java)
        }
    }
}
