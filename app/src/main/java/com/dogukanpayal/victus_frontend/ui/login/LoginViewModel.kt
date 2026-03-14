package com.dogukanpayal.victus_frontend.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.LoginRequest
import com.dogukanpayal.victus_frontend.data.repository.AuthRepository
import com.dogukanpayal.victus_frontend.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginResult = MutableStateFlow<Result<com.dogukanpayal.victus_frontend.data.model.AuthSession>?>(null)
    val loginResult: StateFlow<Result<com.dogukanpayal.victus_frontend.data.model.AuthSession>?> = _loginResult.asStateFlow()

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onRememberMeChanged(rememberMe: Boolean) {
        _rememberMe.value = rememberMe
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun onLoginClicked() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _loginResult.value = Result.failure(Exception("Lütfen tüm alanları doldurun"))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginResult.value = null
            
            val result = authRepository.login(
                LoginRequest(
                    email = _email.value,
                    password = _password.value
                )
            )
            
            _loginResult.value = result
            _isLoading.value = false
        }
    }

    fun clearResult() {
        _loginResult.value = null
    }

    fun onGoogleLoginClicked() {
        println("Google Login Clicked")
    }

    fun onAppleLoginClicked() {
        println("Apple Login Clicked")
    }
}
