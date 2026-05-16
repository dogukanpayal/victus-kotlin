package com.dogukanpayal.victus_frontend.ui.diet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.FoodLogItem
import com.dogukanpayal.victus_frontend.data.model.MealItem
import com.dogukanpayal.victus_frontend.data.model.MealType
import com.dogukanpayal.victus_frontend.data.model.NutritionUiState
import com.dogukanpayal.victus_frontend.data.repository.NutritionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DietViewModel : ViewModel() {

    companion object {
        private const val TAG = "DietViewModel"
    }

    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    private val nutritionRepository = NutritionRepositoryImpl()



    /**
     * Backend'den günlük özet verilerini çek
     * GET /v1/nutrition/summary
     */
    fun loadDailySummary(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "loadDailySummary: Başlatıldı")
            
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val result = nutritionRepository.getDailySummary(token, today)
            
            result.getOrNull()?.let { summary ->
                Log.d(TAG, "loadDailySummary: Başarılı")
                
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
                // Food log'u aynı token ile yükle
                loadFoodLog(token)
            } ?: run {
                val errorMessage = result.exceptionOrNull()?.message ?: "Bilinmeyen bir hata oluştu"
                Log.e(TAG, "loadDailySummary: Hata - $errorMessage")
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

    /**
     * Backend'den bugünkü yemek geçmişini çek (fotoğraf + makrolar için)
     * GET /v1/nutrition/meals?date=TODAY
     */
    fun loadFoodLog(token: String) {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val result = nutritionRepository.getMealHistory(token, today)
            result.getOrNull()?.let { items ->
                Log.d(TAG, "loadFoodLog: ${items.size} öğün yüklendi")
                val mealItems = items.map { foodItem ->
                    MealItem(
                        id = foodItem.id,
                        name = foodItem.foodName,
                        description = "",
                        calories = foodItem.calories,
                        protein = foodItem.protein,
                        carbs = foodItem.carbs,
                        fat = foodItem.fat,
                        mealType = MealType.SNACK, // Default for logs if not specified
                        imageUrl = foodItem.imageUrl,
                        createdAt = foodItem.createdAt
                    )
                }
                _uiState.update { it.copy(foodLog = items, meals = mealItems) }
            } ?: Log.w(TAG, "loadFoodLog: Hata - ${result.exceptionOrNull()?.message}")
        }
    }

    /**
     * Diyet metnini backend'e gönderip parse edilmesini sağlar.
     * POST /v1/diet/parse-and-save
     */
    fun parseAndSaveDiet(token: String, rawText: String, startDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsingDiet = true, dietParsingError = null) }
            
            val result = nutritionRepository.parseAndSaveDiet(token, rawText, startDate, activate = true)
            
            result.onSuccess { response ->
                if (response.success) {
                    _uiState.update { it.copy(isParsingDiet = false, dietParsingError = null) }
                    // Başarılı ise dashboard'u güncelle
                    loadDailySummary(token)
                } else {
                    _uiState.update { 
                        it.copy(
                            isParsingDiet = false, 
                            dietParsingError = response.message ?: "Diyet çözümlenemedi."
                        ) 
                    }
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isParsingDiet = false, 
                        dietParsingError = error.message ?: "Sunucu hatası oluştu."
                    ) 
                }
            }
        }
    }

    fun clearDietParsingError() {
        _uiState.update { it.copy(dietParsingError = null) }
    }

    /**
     * Kullanıcı bir yemeği silmek istediğinde çağrılır:
     * DELETE /v1/nutrition/meal/:id
     */
    fun deleteMealLog(token: String, mealId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = nutritionRepository.deleteMeal(token, mealId)
            
            result.onSuccess {
                Log.d(TAG, "deleteMealLog: Başarılı")
                // Başarılı ise verileri yeniden yükle
                loadDailySummary(token)
            }.onFailure { error ->
                Log.e(TAG, "deleteMealLog: Hata - ${error.message}")
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun resetState() {
        _uiState.value = NutritionUiState()
    }
}




