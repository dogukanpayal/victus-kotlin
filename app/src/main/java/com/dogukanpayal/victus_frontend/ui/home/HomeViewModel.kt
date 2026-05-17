package com.dogukanpayal.victus_frontend.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.NutritionUiState
import com.dogukanpayal.victus_frontend.data.repository.NutritionRepositoryImpl
import com.dogukanpayal.victus_frontend.data.repository.FeedbackRepositoryImpl
import com.dogukanpayal.victus_frontend.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NutritionUiState(isLoading = true))
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    private val nutritionRepository = NutritionRepositoryImpl()
    private val feedbackRepository = FeedbackRepositoryImpl(RetrofitClient.apiService)

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

            // Fetch Feedbacks
            val feedbackResult = feedbackRepository.getFeedbacks(token)
            feedbackResult.onSuccess { feedbacks ->
                _uiState.update { it.copy(feedbacks = feedbacks) }
            }
        }
    }

    fun markFeedbackAsRead(token: String, feedbackId: String) {
        viewModelScope.launch {
            feedbackRepository.markAsRead(token, feedbackId).onSuccess {
                _uiState.update { state ->
                    state.copy(feedbacks = state.feedbacks.map { 
                        if (it.id == feedbackId) it.copy(isRead = true) else it 
                    })
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = NutritionUiState()
    }
}
