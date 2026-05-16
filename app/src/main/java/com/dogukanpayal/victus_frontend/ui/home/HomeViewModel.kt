package com.dogukanpayal.victus_frontend.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.NutritionUiState
import com.dogukanpayal.victus_frontend.data.repository.NutritionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    private val nutritionRepository = NutritionRepositoryImpl()

    fun loadDailySummary(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val result = nutritionRepository.getDailySummary(token, today)
            
            result.getOrNull()?.let { summary ->
                _uiState.update { state ->
                    state.copy(
                        dailyCalorieGoal = summary.dailyGoal,
                        consumedCalories = summary.caloriesConsumed,
                        remainingCalories = summary.caloriesRemaining,
                        proteinConsumed = summary.macros.consumed.protein.toFloat(),
                        proteinGoal = summary.macros.targets.protein.toFloat(),
                        carbsConsumed = summary.macros.consumed.carbs.toFloat(),
                        carbsGoal = summary.macros.targets.carbs.toFloat(),
                        fatConsumed = summary.macros.consumed.fat.toFloat(),
                        fatGoal = summary.macros.targets.fat.toFloat(),
                        isLoading = false,
                        error = null
                    )
                }
            } ?: run {
                val errorMessage = result.exceptionOrNull()?.message ?: "Bilinmeyen bir hata oluştu"
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    fun resetState() {
        _uiState.value = NutritionUiState()
    }
}
