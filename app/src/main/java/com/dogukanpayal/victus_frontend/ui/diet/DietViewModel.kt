package com.dogukanpayal.victus_frontend.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.MealItem
import com.dogukanpayal.victus_frontend.data.model.MealType
import com.dogukanpayal.victus_frontend.data.model.NutritionUiState
import com.dogukanpayal.victus_frontend.data.repository.NutritionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DietViewModel : ViewModel() {

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
            
            val result = nutritionRepository.getDailySummary(token)
            
            result.getOrNull()?.let { summary ->
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
            } ?: run {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Bilinmeyen bir hata oluştu"
                    )
                }
                // Hata durumunda mock veri yükle (fallback)
                loadMockData()
            }
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




