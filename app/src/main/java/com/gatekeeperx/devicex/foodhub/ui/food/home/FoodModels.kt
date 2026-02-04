package com.gatekeeperx.devicex.foodhub.ui.food.home

import androidx.annotation.DrawableRes

/**
 * Food category representation
 */
data class FoodCategory(
    val id: String,
    val name: String,
    @DrawableRes val icon: Int,
    val isSelected: Boolean = false
)

/**
 * Special dish representation
 */
data class SpecialDish(
    val id: String,
    val name: String,
    val price: Double,
    @DrawableRes val imageRes: Int,
    val isFavorite: Boolean = false
)

/**
 * Recommended dish representation
 */
data class RecommendedDish(
    val id: String,
    val name: String,
    @DrawableRes val imageRes: Int
)

/**
 * UI State for Food Delivery Home Screen
 */
data class FoodDeliveryUiState(
    val userName: String = "James",
    val cartItemCount: Int = 2,
    val searchQuery: String = "",
    val categories: List<FoodCategory> = emptyList(),
    val specialDishes: List<SpecialDish> = emptyList(),
    val recommendedDishes: List<RecommendedDish> = emptyList()
)
