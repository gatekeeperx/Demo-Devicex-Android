package com.gatekeeperx.devicex.foodhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gatekeeperx.devicex.foodhub.ui.food.detail.DishDetailsScreen
import com.gatekeeperx.devicex.foodhub.ui.theme.FoodDeliveryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that hosts the Dish Details Compose UI
 */
@AndroidEntryPoint
class DishDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dishId = intent.getStringExtra(EXTRA_DISH_ID) ?: run {
            finish()
            return
        }

        setContent {
            FoodDeliveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DishDetailsScreen(
                        dishId = dishId,
                        onBackClick = { finish() },
                        onCartClick = {
                            // Navigate to checkout cart
                            startActivity(CheckoutCartActivity.createIntent(this))
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_DISH_ID = "dishId"

        fun createIntent(context: Context, dishId: String): Intent {
            return Intent(context, DishDetailsActivity::class.java).apply {
                putExtra(EXTRA_DISH_ID, dishId)
            }
        }
    }
}
