package com.dogukanpayal.victus_frontend.ui.diet

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.dogukanpayal.victus_frontend.data.model.MealItem
import com.dogukanpayal.victus_frontend.data.model.MealType
import com.dogukanpayal.victus_frontend.data.model.NutritionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DietViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * İleride bu fonksiyon repository üzerinden backend'den veri çekecek:
     * GET /api/nutrition/daily-summary
     */
    fun loadDailySummary() {
        // TODO: NutritionRepository.getDailySummary(date) entegrasyonu
        loadMockData()
    }

    /**
     * Kullanıcı AI sonucunu onayladığında çağrılacak:
     * POST /api/nutrition/save
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
