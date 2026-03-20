package com.fixmybill.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class OtpSent(val phoneNumber: String) : AuthState()
    data class Success(val userId: String, val displayName: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var pendingPhoneNumber: String = ""

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Mock implementation - simulate network delay
                delay(1500)
                // In production, this would call FirebaseAuth.signInWithCredential()
                _authState.value = AuthState.Success(
                    userId = "google_user_${idToken.take(8)}",
                    displayName = "Google User"
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    message = e.message ?: "Google sign-in failed"
                )
            }
        }
    }

    fun signInWithPhone(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Mock implementation - simulate sending OTP
                delay(1000)
                pendingPhoneNumber = phoneNumber
                // In production, this would call PhoneAuthProvider.verifyPhoneNumber()
                _authState.value = AuthState.OtpSent(phoneNumber)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    message = e.message ?: "Failed to send OTP"
                )
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Mock implementation - simulate OTP verification
                delay(1000)
                if (otp.length == 6) {
                    // In production, this would call PhoneAuthProvider.getCredential() + signIn
                    _authState.value = AuthState.Success(
                        userId = "phone_user_${pendingPhoneNumber.takeLast(4)}",
                        displayName = null
                    )
                } else {
                    _authState.value = AuthState.Error(message = "Invalid OTP. Please enter a 6-digit code.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    message = e.message ?: "OTP verification failed"
                )
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        pendingPhoneNumber = ""
    }
}
