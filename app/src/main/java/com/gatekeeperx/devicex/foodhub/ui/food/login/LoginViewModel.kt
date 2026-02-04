package com.gatekeeperx.devicex.foodhub.ui.food.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gatekeeperx.android.devicex.Devicex
import com.gatekeeperx.android.devicex.data.EventResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Login Screen
 */
data class LoginUiState(
    val email: String = "demo@foodhub.com",
    val password: String = "demo123",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

/**
 * ViewModel for Login Screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onLoginClick() {
        val currentState = _uiState.value

        // Validation
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email") }
            return
        }

        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your password") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email") }
            return
        }

        // Validate hardcoded credentials
        if (currentState.email != DEMO_EMAIL || currentState.password != DEMO_PASSWORD) {
            _uiState.update { it.copy(errorMessage = "Invalid email or password") }
            return
        }

        // Start loading
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Send login event to Devicex
        viewModelScope.launch {
            try {
                // Check if Devicex is initialized
                if (!Devicex.isInitialized()) {
                    Log.e(TAG, "✗ Devicex SDK not initialized")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Devicex SDK not initialized"
                        )
                    }
                    return@launch
                }

                // Build payload for logging
                val eventProperties = mapOf(
                    "customerID" to "abcdefghijk123456789",
                    "sessionID" to "1234567890abcdefghijk",
                    "timestamp" to System.currentTimeMillis(),
                    "screen" to "LoginScreen",
                    "source" to "food-hub"
                )


                Devicex.sendEventAsync(
                    name = "login",
                    properties = eventProperties
                )
                { result ->
                    when (result) {
                        is EventResult.Success -> {
                            Log.d(TAG, "✓ Login event sent - DeviceXId: ${result.deviceXId}")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoginSuccessful = true,
                                    errorMessage = null
                                )
                            }
                        }

                        is EventResult.Failure -> {
                            Log.e(TAG, "✗ Login event failed - ${result.errorMessage}")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoginSuccessful = false,
                                    errorMessage = result.errorMessage
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Login error: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "An error occurred. Please try again."
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
        private const val DEMO_EMAIL = "demo@foodhub.com"
        private const val DEMO_PASSWORD = "demo123"
    }
}
