package com.dogukanpayal.victus_frontend.data.model

data class NutritionUiState(
    val dailyCalorieGoal: Int = 2200,
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 2200,
    val proteinConsumed: Float = 0f,
    val proteinGoal: Float = 146f,
    val carbsConsumed: Float = 0f,
    val carbsGoal: Float = 275f,
    val fatConsumed: Float = 0f,
    val fatGoal: Float = 73f,
    val meals: List<MealItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
