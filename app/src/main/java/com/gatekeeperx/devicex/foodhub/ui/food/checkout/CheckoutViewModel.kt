package com.gatekeeperx.devicex.foodhub.ui.food.checkout

import androidx.lifecycle.ViewModel
import com.gatekeeperx.devicex.foodhub.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the checkout screen
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private fun createInitialState(): CheckoutUiState {
        // Mock data - 3 items in cart
        val mockItems = listOf(
            CartItem(
                id = "noodles",
                name = "Noodles",
                description = "with shrimps,egg,pork",
                price = 7.50,
                quantity = 1,
                imageRes = R.drawable.img_noodles
            ),
            CartItem(
                id = "fruits_salad",
                name = "Fruits Salad",
                description = "with strawberry,tomato,egg",
                price = 7.50,
                quantity = 1,
                imageRes = R.drawable.img_fruits_salad
            ),
            CartItem(
                id = "curry",
                name = "Curry",
                description = "with strawberry,tomato,egg",
                price = 7.50,
                quantity = 1,
                imageRes = R.drawable.img_curry
            )
        )

        return CheckoutUiState.calculateTotals(mockItems)
    }

    /**
     * Increase quantity of an item
     */
    fun increaseQuantity(itemId: String) {
        val currentState = _uiState.value
        val updatedItems = currentState.cartItems.map { item ->
            if (item.id == itemId) {
                item.copy(quantity = item.quantity + 1)
            } else {
                item
            }
        }
        _uiState.value = CheckoutUiState.calculateTotals(updatedItems)
    }

    /**
     * Decrease quantity of an item (minimum 1)
     */
    fun decreaseQuantity(itemId: String) {
        val currentState = _uiState.value
        val updatedItems = currentState.cartItems.map { item ->
            if (item.id == itemId && item.quantity > 1) {
                item.copy(quantity = item.quantity - 1)
            } else {
                item
            }
        }
        _uiState.value = CheckoutUiState.calculateTotals(updatedItems)
    }

    /**
     * Remove item from cart
     */
    fun removeItem(itemId: String) {
        val currentState = _uiState.value
        val updatedItems = currentState.cartItems.filter { it.id != itemId }
        _uiState.value = CheckoutUiState.calculateTotals(updatedItems)
    }
}
