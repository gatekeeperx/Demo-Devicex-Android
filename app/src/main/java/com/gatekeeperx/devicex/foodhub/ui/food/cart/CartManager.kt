package com.gatekeeperx.devicex.foodhub.ui.food.cart

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton to manage cart state across the app
 */
object CartManager {
    private val _cartItemCount = MutableStateFlow(3) // Start with 3 items (as per design)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    fun addItem() {
        _cartItemCount.value += 1
    }

    fun removeItem() {
        if (_cartItemCount.value > 0) {
            _cartItemCount.value -= 1
        }
    }

    fun setItemCount(count: Int) {
        _cartItemCount.value = count.coerceAtLeast(0)
    }

    fun getItemCount(): Int = _cartItemCount.value
}
