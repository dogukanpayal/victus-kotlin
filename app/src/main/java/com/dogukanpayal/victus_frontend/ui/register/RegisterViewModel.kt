package com.dogukanpayal.victus_frontend.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.repository.AuthRepository
import com.dogukanpayal.victus_frontend.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl() // Default for now
) : ViewModel() {
    private val TAG = "VictusAuth"
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _termsAccepted = MutableStateFlow(false)
    val termsAccepted: StateFlow<Boolean> = _termsAccepted.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _registrationResult = MutableStateFlow<Result<com.dogukanpayal.victus_frontend.data.model.AuthSession>?>(null)
    val registrationResult: StateFlow<Result<com.dogukanpayal.victus_frontend.data.model.AuthSession>?> = _registrationResult.asStateFlow()

    fun onFullNameChanged(newName: String) {
        _fullName.value = newName
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun onTermsAcceptedChanged(accepted: Boolean) {
        _termsAccepted.value = accepted
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun onRegisterClicked() {
        Log.d(TAG, "Register button clicked. Name: ${fullName.value}, Email: ${email.value}")
        if (!termsAccepted.value) {
            Log.w(TAG, "Registration attempt failed: Terms not accepted")
            return
        }

        viewModelScope.launch {
            val result = authRepository.register(email.value, password.value, fullName.value)
            _registrationResult.value = result
            result.onSuccess {
                Log.d(TAG, "Registration ViewModel: Success for ${email.value}")
            }.onFailure {
                Log.e(TAG, "Registration ViewModel: Failure - ${it.message}")
            }
        }
    }

    fun clearResult() {
        _registrationResult.value = null
    }

    fun onGoogleRegisterClicked() {
        // Handle Google registration
    }

    fun onAppleRegisterClicked() {
        // Handle Apple registration
    }
}
