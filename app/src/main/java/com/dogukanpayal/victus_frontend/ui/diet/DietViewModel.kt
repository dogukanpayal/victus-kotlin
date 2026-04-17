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

    init {
        loadMockData()
    }

    /**
     * Backend'den günlük özet verilerini çek
     * GET /v1/nutrition/summary
     */
    fun loadDailySummary(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "loadDailySummary: Başlatıldı")
            
            val result = nutritionRepository.getDailySummary(token)
            
            result.getOrNull()?.let { summary ->
                Log.d(TAG, "loadDailySummary: Başarılı")
                Log.d(TAG, "Summary - Daily Goal: ${summary.dailyGoal}, Consumed: ${summary.caloriesConsumed}")
                Log.d(TAG, "Macros - Protein: ${summary.macros.proteinConsumed}/${summary.macros.proteinGoal}, " +
                    "Carbs: ${summary.macros.carbsConsumed}/${summary.macros.carbsGoal}, " +
                    "Fat: ${summary.macros.fatConsumed}/${summary.macros.fatGoal}")
                
                _uiState.update { state ->
                    state.copy(
                        dailyCalorieGoal = summary.dailyGoal,
                        consumedCalories = summary.caloriesConsumed,
                        remainingCalories = summary.caloriesRemaining,
                        proteinConsumed = summary.macros.proteinConsumed.toFloat(),
                        proteinGoal = summary.macros.proteinGoal.toFloat(),
                        carbsConsumed = summary.macros.carbsConsumed.toFloat(),
                        carbsGoal = summary.macros.carbsGoal.toFloat(),
                        fatConsumed = summary.macros.fatConsumed.toFloat(),
                        fatGoal = summary.macros.fatGoal.toFloat(),
                        isLoading = false,
                        error = null
                    )
                }
                // Food log'u aynı token ile yükle
                loadFoodLog(token)
            } ?: run {
                val errorMessage = result.exceptionOrNull()?.message ?: "Bilinmeyen bir hata oluştu"
                Log.e(TAG, "loadDailySummary: Hata - $errorMessage", result.exceptionOrNull())
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                // Hata durumunda mock veri yükle (fallback)
                loadMockData()
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
                _uiState.update { it.copy(foodLog = items) }
            } ?: Log.w(TAG, "loadFoodLog: Hata - ${result.exceptionOrNull()?.message}")
        }
    }

    /**
     * Kullanıcı AI sonucunu onayladığında çağrılacak:
     * POST /v1/nutrition/save
     */
    fun addMeal(meal: MealItem) {
        _uiState.update { state ->
            val updatedMeals = state.meals + meal
            recalculateState(state, updatedMeals)
        }
    }

    /**
     * Öğün silme (gelecek feature)
     */
    fun removeMeal(mealId: String) {
        _uiState.update { state ->
            val updatedMeals = state.meals.filter { it.id != mealId }
            recalculateState(state, updatedMeals)
        }
    }

    private fun recalculateState(state: NutritionUiState, meals: List<MealItem>): NutritionUiState {
        val consumed = meals.sumOf { it.calories }
        val protein = meals.map { it.protein }.sum()
        val carbs = meals.map { it.carbs }.sum()
        val fat = meals.map { it.fat }.sum()

        return state.copy(
            meals = meals,
            consumedCalories = consumed,
            remainingCalories = (state.dailyCalorieGoal - consumed).coerceAtLeast(0),
            proteinConsumed = protein,
            carbsConsumed = carbs,
            fatConsumed = fat
        )
    }

    private fun loadMockData() {
        val mockMeals = listOf(
            MealItem(
                id = "1",
                name = "Kahvaltı",
                description = "Yulaf Ezmesi, Yumurta, Meyve",
                calories = 420,
                protein = 28f,
                carbs = 45f,
                fat = 14f,
                mealType = MealType.BREAKFAST,
                portionSize = 1.0f
            ),
            MealItem(
                id = "2",
                name = "Öğle Yemeği",
                description = "Izgara Tavuk, Kinoa Salata",
                calories = 580,
                protein = 42f,
                carbs = 55f,
                fat = 18f,
                mealType = MealType.LUNCH,
                portionSize = 1.0f
            ),
            MealItem(
                id = "3",
                name = "Akşam Yemeği",
                description = "Somon Füme, Kuşkonmaz",
                calories = 350,
                protein = 30f,
                carbs = 20f,
                fat = 16f,
                mealType = MealType.DINNER,
                portionSize = 1.0f
            ),
            MealItem(
                id = "4",
                name = "Atıştırmalık",
                description = "Çiğ Badem, Yoğurt",
                calories = 100,
                protein = 8f,
                carbs = 12f,
                fat = 4f,
                mealType = MealType.SNACK,
                portionSize = 1.0f
            )
        )

        _uiState.update { state ->
            recalculateState(state, mockMeals)
        }
    }
}




