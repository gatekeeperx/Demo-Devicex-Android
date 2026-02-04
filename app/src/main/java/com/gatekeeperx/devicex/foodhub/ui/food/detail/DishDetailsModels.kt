package com.gatekeeperx.devicex.foodhub.ui.food.detail

import androidx.annotation.DrawableRes

/**
 * Dish details representation
 */
data class DishDetails(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val weight: String,
    val calories: String,
    val portion: String,
    @DrawableRes val imageRes: Int,
    val restaurant: Restaurant,
    val rating: Float = 5f
)

/**
 * Restaurant information
 */
data class Restaurant(
    val name: String,
    val distance: String,
    @DrawableRes val logoRes: Int
)

/**
 * UI State for Dish Details Screen
 */
data class DishDetailsUiState(
    val dish: DishDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartItemCount: Int = 2
)
