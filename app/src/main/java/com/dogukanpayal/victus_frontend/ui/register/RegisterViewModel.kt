package com.dogukanpayal.victus_frontend.ui.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {
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
        // Handle registration logic here later (no backend integration yet)
        println("Register clicked with: Name=${fullName.value}, Email=${email.value}, Password=${password.value}, Terms=${termsAccepted.value}")
    }

    fun onGoogleRegisterClicked() {
        // Handle Google registration
    }

    fun onAppleRegisterClicked() {
        // Handle Apple registration
    }
}
