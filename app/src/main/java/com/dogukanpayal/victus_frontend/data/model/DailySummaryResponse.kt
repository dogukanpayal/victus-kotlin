package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

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

data class MacrosSummary(
    @Json(name = "protein_consumed")
    val proteinConsumed: Double,
    
    @Json(name = "protein_goal")
    val proteinGoal: Double,
    
    @Json(name = "carbs_consumed")
    val carbsConsumed: Double,
    
    @Json(name = "carbs_goal")
    val carbsGoal: Double,
    
    @Json(name = "fat_consumed")
    val fatConsumed: Double,
    
    @Json(name = "fat_goal")
    val fatGoal: Double
)

