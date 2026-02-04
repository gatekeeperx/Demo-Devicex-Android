package com.gatekeeperx.devicex.foodhub.ui.food.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gatekeeperx.android.devicex.Devicex
import com.gatekeeperx.android.devicex.data.EventResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Order status
 */
enum class OrderStatus {
    PROCESSING,
    PREPARING,
    ON_THE_WAY,
    DELIVERED
}

/**
 * UI State for Order Screen
 */
data class OrderUiState(
    val orderNumber: String = "#${(1000..9999).random()}",
    val status: OrderStatus = OrderStatus.PROCESSING,
    val timeRemaining: Int = 10,
    val estimatedTime: String = "10 seconds",
    val totalAmount: Double = 22.50,
    val itemCount: Int = 3,
    val isCompleted: Boolean = false
)

/**
 * ViewModel for Order Tracking Screen
 */
@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        startOrderTracking()
        sendCheckoutEvent()
    }

    private fun startOrderTracking() {
        viewModelScope.launch {
            // Total 10 seconds divided into phases
            // 0-3s: Processing
            _uiState.update { it.copy(status = OrderStatus.PROCESSING) }
            delay(3000)

            // 3-5s: Preparing
            _uiState.update { it.copy(status = OrderStatus.PREPARING, timeRemaining = 7) }
            delay(2000)

            // 5-8s: On the way
            _uiState.update { it.copy(status = OrderStatus.ON_THE_WAY, timeRemaining = 5) }
            delay(3000)

            // 8-10s: Delivered
            _uiState.update { it.copy(status = OrderStatus.DELIVERED, timeRemaining = 2) }
            delay(2000)

            // Mark as completed
            _uiState.update { it.copy(isCompleted = true, timeRemaining = 0) }

            // Track delivery event
            sendDeliveryEvent()
        }
    }

    private fun sendCheckoutEvent() {
        viewModelScope.launch {
            try {
                val eventProperties = mapOf(
                    "customerID" to "abcdefghijk123456789",
                    "sessionID" to "1234567890abcdefghijk",
                    "order_number" to _uiState.value.orderNumber,
                    "total_amount" to _uiState.value.totalAmount,
                    "item_count" to _uiState.value.itemCount,
                    "payment_method" to "credit_card",
                    "source" to "food-hub"
                )

                Devicex.sendEventAsync(
                    name = "checkout",
                    properties = eventProperties
                ) { result ->
                    when (result) {
                        is EventResult.Success -> {
                            Log.d(TAG, "✓ Checkout event sent - Order: ${_uiState.value.orderNumber}, DeviceXId: ${result.deviceXId}")
                        }

                        is EventResult.Failure -> {
                            Log.e(TAG, "✗ Checkout event failed - ${result.errorMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Checkout event error: ${e.message}")
            }
        }
    }

    private fun sendDeliveryEvent() {
        viewModelScope.launch {
            try {
                val eventProperties = mapOf(
                    "order_number" to _uiState.value.orderNumber,
                    "delivery_time" to "10 seconds",
                    "status" to "delivered",
                    "source" to "demo-app"
                )

                Devicex.sendEventAsync(
                    name = "order_delivered",
                    properties = eventProperties
                ) { result ->
                    when (result) {
                        is EventResult.Success -> {
                            Log.d(TAG, "✓ Order delivered event sent - Order: ${_uiState.value.orderNumber}, DeviceXId: ${result.deviceXId}")
                        }

                        is EventResult.Failure -> {
                            Log.e(TAG, "✗ Order delivered event failed - ${result.errorMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Delivery event error: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "OrderViewModel"
    }
}
