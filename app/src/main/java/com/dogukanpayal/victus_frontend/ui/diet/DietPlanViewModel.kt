package com.dogukanpayal.victus_frontend.ui.diet

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.DietPlan
import com.dogukanpayal.victus_frontend.data.repository.DietPlanRepository
import com.dogukanpayal.victus_frontend.data.repository.DietPlanRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DietPlanUiState(
    val plan: DietPlan? = null,
    val selectedDayIndex: Int = 0,
    val isUploading: Boolean = false,
    val uploadError: String? = null,
    val showUploadSheet: Boolean = false
)

class DietPlanViewModel(
    context: Context,
    private val repository: DietPlanRepository = DietPlanRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietPlanUiState(selectedDayIndex = getTodayIndex()))
    val uiState: StateFlow<DietPlanUiState> = _uiState.asStateFlow()

    init {
        loadSavedPlan()
    }

    private fun getTodayIndex(): Int {
        // LocalDate.now().dayOfWeek.value döner (Pazartesi=1, Pazar=7)
        // Bizim UI'da Pazartesi=0 olduğu için -1 yapıyoruz
        return LocalDate.now().dayOfWeek.value - 1
    }

    private fun loadSavedPlan() {
        viewModelScope.launch {
            val savedPlan = repository.getSavedPlan()
            _uiState.value = _uiState.value.copy(plan = savedPlan)
        }
    }

    fun onUploadRequested() {
        _uiState.value = _uiState.value.copy(showUploadSheet = true)
    }

    fun dismissUploadSheet() {
        _uiState.value = _uiState.value.copy(showUploadSheet = false)
    }

    fun onFileSelected(context: Context, uri: Uri, mimeType: String, token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploading = true,
                uploadError = null,
                showUploadSheet = false
            )

            val result = repository.analyzeDietPlan(context, uri, mimeType, token)

            result.onSuccess { newPlan ->
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    plan = newPlan,
                    selectedDayIndex = getTodayIndex() // Yeni plan yüklendiğinde bugüne odaklan
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    uploadError = exception.message ?: "Plan yüklenirken hata oluştu"
                )
            }
        }
    }

    fun onDaySelected(index: Int) {
        if (index in 0..6) {
            _uiState.value = _uiState.value.copy(selectedDayIndex = index)
        }
    }

    fun onDeletePlan() {
        viewModelScope.launch {
            repository.clearPlan()
            _uiState.value = _uiState.value.copy(plan = null)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(uploadError = null)
    }
}
