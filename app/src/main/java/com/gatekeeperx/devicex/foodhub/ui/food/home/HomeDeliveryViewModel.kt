package com.gatekeeperx.devicex.foodhub.ui.food.home

import android.util.Log
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
 * ViewModel for Food Delivery Home Screen
 */
@HiltViewModel
class HomeDeliveryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FoodDeliveryUiState())
    val uiState: StateFlow<FoodDeliveryUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
        observeCartCount()
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            CartManager.cartItemCount.collect { count ->
                _uiState.update { it.copy(cartItemCount = count) }
            }
        }
    }

    private fun loadMockData() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    userName = "James",
                    cartItemCount = CartManager.getItemCount(),
                    categories = getMockCategories(),
                    specialDishes = getMockSpecialDishes(),
                    recommendedDishes = getMockRecommendedDishes()
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                categories = currentState.categories.map { category ->
                    category.copy(isSelected = category.id == categoryId)
                }
            )
        }

        // Track category selection event
        trackCategorySelection(categoryId)
    }

    fun onDishSelected(dishId: String, dishName: String, dishPrice: Double) {
        // Track dish/product selection event
        trackProductView(dishId, dishName, dishPrice)
    }

    fun onDishFavoriteToggled(dishId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                specialDishes = currentState.specialDishes.map { dish ->
                    if (dish.id == dishId) {
                        dish.copy(isFavorite = !dish.isFavorite)
                    } else {
                        dish
                    }
                }
            )
        }
    }

    /**
     * Track when user views a product
     */
    private fun trackProductView(dishId: String, dishName: String, dishPrice: Double) {
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
                    "product_id" to dishId,
                    "product_name" to dishName,
                    "product_price" to dishPrice,
                    "timestamp" to System.currentTimeMillis(),
                    "screen" to "HomeDeliveryScreen",
                    "source" to "food-hub"
                )

                Devicex.sendEventAsync(
                    name = "product_view",
                    properties = eventProperties
                ) { result ->
                    when (result) {
                        is EventResult.Success -> {
                            Log.d(TAG, "✓ Product view event sent - Product: $dishName, DeviceXId: ${result.deviceXId}")
                        }

                        is EventResult.Failure -> {
                            Log.e(TAG, "✗ Product view event failed - ${result.errorMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Product view tracking error: ${e.message}")
            }
        }
    }

    /**
     * Track category selection
     */
    private fun trackCategorySelection(categoryId: String) {
        viewModelScope.launch {
            try {
                if (!Devicex.isInitialized()) {
                    Log.e(TAG, "✗ Devicex SDK not initialized")
                    return@launch
                }

                val eventProperties = mapOf(
                    "category_id" to categoryId,
                    "timestamp" to System.currentTimeMillis(),
                    "screen" to "HomeDeliveryScreen",
                    "source" to "demo-app"
                )

                Devicex.sendEventAsync(
                    name = "category_selected",
                    properties = eventProperties
                ) { result ->
                    when (result) {
                        is EventResult.Success -> {
                            Log.d(TAG, "✓ Category selected event sent - Category: $categoryId, DeviceXId: ${result.deviceXId}")
                        }

                        is EventResult.Failure -> {
                            Log.e(TAG, "✗ Category selected event failed - ${result.errorMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Category selection tracking error: ${e.message}")
            }
        }
    }

    private fun getMockCategories(): List<FoodCategory> {
        return listOf(
            FoodCategory(
                id = "all",
                name = "All",
                icon = R.drawable.ic_all,
                isSelected = false
            ),
            FoodCategory(
                id = "italian",
                name = "Italian",
                icon = R.drawable.ic_italian,
                isSelected = false
            ),
            FoodCategory(
                id = "thai",
                name = "Thai",
                icon = R.drawable.ic_thai,
                isSelected = true
            ),
            FoodCategory(
                id = "asian",
                name = "Asian",
                icon = R.drawable.ic_asian,
                isSelected = false
            )
        )
    }

    private fun getMockSpecialDishes(): List<SpecialDish> {
        return listOf(
            SpecialDish(
                id = "noodles",
                name = "Noodles",
                price = 7.2,
                imageRes = R.drawable.img_noodles,
                isFavorite = false
            ),
            SpecialDish(
                id = "pasta",
                name = "Pasta",
                price = 6.2,
                imageRes = R.drawable.img_pasta,
                isFavorite = false
            )
        )
    }

    private fun getMockRecommendedDishes(): List<RecommendedDish> {
        return listOf(
            RecommendedDish(
                id = "rec1",
                name = "Raspberry Cake",
                imageRes = R.drawable.img_recommended_1
            ),
            RecommendedDish(
                id = "rec2",
                name = "Grilled Steak",
                imageRes = R.drawable.img_recommended_2
            ),
            RecommendedDish(
                id = "rec3",
                name = "Chicken Special",
                imageRes = R.drawable.img_recommended_3
            )
        )
    }

    companion object {
        private const val TAG = "HomeDeliveryViewModel"
    }
}
