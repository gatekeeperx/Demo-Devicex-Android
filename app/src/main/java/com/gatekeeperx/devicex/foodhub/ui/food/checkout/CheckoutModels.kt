package com.gatekeeperx.devicex.foodhub.ui.food.checkout

import androidx.annotation.DrawableRes

/**
 * Data model for cart items in the checkout screen
 */
data class CartItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    @DrawableRes val imageRes: Int
)

/**
 * UI state for the checkout screen
 */
data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val cartItemCount: Int = 0,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 2.50,
    val tax: Double = 0.0,
    val total: Double = 0.0
) {
    companion object {
        fun calculateTotals(items: List<CartItem>, deliveryFee: Double = 2.50): CheckoutUiState {
            val subtotal = items.sumOf { it.price * it.quantity }
            val tax = subtotal * 0.08 // 8% tax
            val total = subtotal + deliveryFee + tax
            val itemCount = items.sumOf { it.quantity }

            return CheckoutUiState(
                cartItems = items,
                cartItemCount = itemCount,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                tax = tax,
                total = total
            )
        }
    }
}
