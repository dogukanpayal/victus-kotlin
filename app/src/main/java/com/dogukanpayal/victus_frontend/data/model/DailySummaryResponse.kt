package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Daily nutrition summary from backend
 * Maps to /v1/nutrition/summary endpoint response
 */
@JsonClass(generateAdapter = true)
data class DailySummaryResponse(
    @Json(name = "daily_goal")
    val dailyGoal: Int,
    
    @Json(name = "calories_consumed")
    val caloriesConsumed: Int,
    
    @Json(name = "calories_remaining")
    val caloriesRemaining: Int,
    
    @Json(name = "calories_burned")
    val caloriesBurned: Double? = 0.0,
    
    @Json(name = "macros")
    val macros: MacrosBreakdown,

    @Json(name = "date")
    val date: String
)

@JsonClass(generateAdapter = true)
data class MacrosBreakdown(
    @Json(name = "targets")
    val targets: MacroValues,
    
    @Json(name = "consumed")
    val consumed: MacroValues,
    
    @Json(name = "remaining")
    val remaining: MacroValues
)

@JsonClass(generateAdapter = true)
data class MacroValues(
    @Json(name = "protein")
    val protein: Double = 0.0,

    @Json(name = "carbs")
    val carbs: Double = 0.0,

    @Json(name = "fat")
    val fat: Double = 0.0,

    @Json(name = "calories")
    val calories: Int = 0
)

