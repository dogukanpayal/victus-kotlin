package com.dogukanpayal.victus_frontend.ui.dietitian_link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LinkDietitianState {
    object Idle : LinkDietitianState()
    object Loading : LinkDietitianState()
    object Success : LinkDietitianState()
    data class Error(val message: String) : LinkDietitianState()
}

class LinkDietitianViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LinkDietitianState>(LinkDietitianState.Idle)
    val uiState: StateFlow<LinkDietitianState> = _uiState.asStateFlow()

    fun linkDietitian(token: String, code: String) {
        if (code.isBlank()) {
            _uiState.value = LinkDietitianState.Error("Lütfen geçerli bir kod giriniz.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LinkDietitianState.Loading
            val result = profileRepository.linkDietitian(token, code)
            result.onSuccess {
                _uiState.value = LinkDietitianState.Success
            }.onFailure { error ->
                val errorMsg = error.message ?: ""
                val displayMsg = when {
                    errorMsg.contains("zaten", ignoreCase = true) -> "Bu diyetisyenle zaten eşleşmiş durumdasınız."
                    errorMsg.contains("geçersiz", ignoreCase = true) || errorMsg.contains("bulunamadı", ignoreCase = true) -> "Geçersiz veya hatalı kod girdiniz."
                    else -> "Bağlantı hatası, lütfen tekrar deneyin."
                }
                _uiState.value = LinkDietitianState.Error(displayMsg)
            }
        }
    }

    fun resetState() {
        _uiState.value = LinkDietitianState.Idle
    }
}
