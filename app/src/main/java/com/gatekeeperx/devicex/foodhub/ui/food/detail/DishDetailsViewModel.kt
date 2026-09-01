package com.gatekeeperx.devicex.foodhub.ui.food.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gatekeeperx.android.devicex.Devicex
import com.gatekeeperx.android.devicex.data.EventResult
import com.gatekeeperx.devicex.foodhub.R
import com.gatekeeperx.devicex.foodhub.ui.food.cart.CartManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Dish Details Screen
 */
@HiltViewModel
class DishDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dishId: String = savedStateHandle.get<String>("dishId") ?: "noodles"

    private val _uiState = MutableStateFlow(DishDetailsUiState())
    val uiState: StateFlow<DishDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDishDetails()
        // Observe cart changes
        observeCartCount()
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            CartManager.cartItemCount.collect { count ->
                _uiState.update { it.copy(cartItemCount = count) }
            }
        }
    }

    private fun loadDishDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Simulate loading from API
                val dishDetails = getMockDishDetails(dishId)
                _uiState.update {
                    it.copy(
                        dish = dishDetails,
                        isLoading = false,
                        error = null,
                        cartItemCount = CartManager.getItemCount()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun onAddToCart() {
        val dish = _uiState.value.dish
        if (dish != null) {
            // Track add to cart event
            trackAddToCart(dish)

            // Update cart count through CartManager
            CartManager.addItem()
        }
    }

    /**
     * Track when user adds item to cart
     */
    private fun trackAddToCart(dish: DishDetails) {
        viewModelScope.launch {
            try {
                // Check if Devicex is initialized
                if (!Devicex.isInitialized()) {
                    Log.e(TAG, "✗ Devicex SDK not initialized")
                    return@launch
                }

                // Build payload for logging
                val eventProperties = mapOf(
                    "customerID" to "abcdefghijk123456789",
                    "sessionID" to "1234567890abcdefghijk",
                    "product_id" to dish.id,
                    "product_name" to dish.name,
                    "product_price" to dish.price,
                    "quantity" to 1,
                    "timestamp" to System.currentTimeMillis(),
                    "screen" to "DishDetailsScreen",
                    "source" to "food-hub"
                )


                Devicex.sendEventAsync(
                    name = "add_to_cart",
                    properties = eventProperties
                ) { result ->
                    when (result) {
                        is EventResult.Success -> {
                            Log.d(TAG, "✓ Add to cart event sent - Product: ${dish.name}, DeviceXId: ${result.deviceXId}")
                            Log.d(TAG, "✓ Add to cart event sent - Product: ${dish.name}, DeviceXSessionId: ${result.deviceXSessionId}")
                        }

                        is EventResult.Failure -> {
                            Log.e(TAG, "✗ Add to cart event failed - ${result.errorMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Add to cart tracking error: ${e.message}")
            }
        }
    }

    private fun getMockDishDetails(dishId: String): DishDetails {
        return when (dishId) {
            "noodles" -> DishDetails(
                id = "noodles",
                name = "Noodles",
                description = "Rice Noodles with shrimps,egg,pork,choy,cabbage.Noodles fave or trying something completely new, we want your tastebuds to be your happy buds.",
                price = 7.50,
                originalPrice = 9.20,
                weight = "300g",
                calories = "530 kcal",
                portion = "1 portion",
                imageRes = R.drawable.img_noodles,
                restaurant = Restaurant(
                    name = "Chin Club",
                    distance = "3.1 km from you",
                    logoRes = R.drawable.img_restaurant_logo
                ),
                rating = 5f
            )

            "pasta" -> DishDetails(
                id = "pasta",
                name = "Pasta",
                description = "Delicious pasta with fresh vegetables, tomato sauce, and herbs. Made with love and passion for great food.",
                price = 6.20,
                originalPrice = 8.00,
                weight = "350g",
                calories = "480 kcal",
                portion = "1 portion",
                imageRes = R.drawable.img_pasta,
                restaurant = Restaurant(
                    name = "Italian Corner",
                    distance = "2.5 km from you",
                    logoRes = R.drawable.img_restaurant_logo
                ),
                rating = 5f
            )

            else -> throw IllegalArgumentException("Unknown dish ID: $dishId")
        }
    }

    companion object {
        private const val TAG = "DishDetailsViewModel"
    }
}

