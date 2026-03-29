package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

/**
 * Daily nutrition summary from backend
 * Maps to /v1/nutrition/summary endpoint response
 * 
 * @Json annotations must match backend field names exactly
 * If mapping fails, check Logcat from NutritionRepository for actual response structure
 */
data class DailySummaryResponse(
    @Json(name = "daily_goal")
    val dailyGoal: Int,
    
    @Json(name = "calories_consumed")
    val caloriesConsumed: Int,
    
    @Json(name = "calories_remaining")
    val caloriesRemaining: Int,
    
    @Json(name = "macros")
    val macros: MacrosSummary
)

/**
 * Macro nutrients summary
 * Maps to backend macros object with protein, carbs, fat fields
 * Goal values are currently mocked (150g protein, 250g carbs, 70g fat)
 * Once backend provides dynamic goals, update @Json names and remove hardcoded values
 */
data class MacrosSummary(
    @Json(name = "protein")
    val proteinConsumed: Double = 0.0,

    @Json(name = "carbs")
    val carbsConsumed: Double = 0.0,

    @Json(name = "fat")
    val fatConsumed: Double = 0.0,

    // Temporary hardcoded goals - backend doesn't send these yet
    // TODO: Update @Json annotations once backend provides dynamic goal values
    val proteinGoal: Double = 150.0,
    val carbsGoal: Double = 250.0,
    val fatGoal: Double = 70.0
)

