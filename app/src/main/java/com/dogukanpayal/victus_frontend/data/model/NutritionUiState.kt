package com.dogukanpayal.victus_frontend.data.model

data class NutritionUiState(
    val dailyCalorieGoal: Int = 0,
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 0,
    val proteinConsumed: Float = 0f,
    val proteinGoal: Float = 0f,
    val carbsConsumed: Float = 0f,
    val carbsGoal: Float = 0f,
    val fatConsumed: Float = 0f,
    val fatGoal: Float = 0f,
    val meals: List<MealItem> = emptyList(),
    val foodLog: List<FoodLogItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Diet parsing specific states
    val isParsingDiet: Boolean = false,
    val dietParsingError: String? = null
)
