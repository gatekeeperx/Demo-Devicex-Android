package com.gatekeeperx.devicex.foodhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gatekeeperx.devicex.foodhub.ui.food.checkout.CheckoutScreen
import com.gatekeeperx.devicex.foodhub.ui.theme.FoodDeliveryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that displays the checkout/cart screen
 */
@AndroidEntryPoint
class CheckoutCartActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodDeliveryTheme {
                CheckoutScreen(
                    onBackClick = { finish() },
                    onCartClick = { /* Already in cart */ },
                    onCheckoutClick = {
                        // Navigate to order tracking
                        startActivity(OrderTrackingActivity.createIntent(this))
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        /**
         * Create intent to launch CheckoutCartActivity
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, CheckoutCartActivity::class.java)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutCartActivityPreview() {
    FoodDeliveryTheme {
        CheckoutScreen()
    }
}
