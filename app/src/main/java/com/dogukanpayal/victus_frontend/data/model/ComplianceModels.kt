package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DietTargetDTO(
    @Json(name = "target_calories") val targetCalories: Double,
    @Json(name = "target_protein") val targetProtein: Double,
    @Json(name = "target_carbs") val targetCarbs: Double,
    @Json(name = "target_fat") val targetFat: Double
)

@JsonClass(generateAdapter = true)
data class ConsumedMealDTO(
    @Json(name = "name") val name: String,
    @Json(name = "calories") val calories: Double,
    @Json(name = "protein") val protein: Double,
    @Json(name = "carbs") val carbs: Double,
    @Json(name = "fat") val fat: Double
)

@JsonClass(generateAdapter = true)
data class ComplianceRequest(
    @Json(name = "consumed_meals") val consumedMeals: List<ConsumedMealDTO>,
    @Json(name = "daily_target") val dailyTarget: DietTargetDTO
)

@JsonClass(generateAdapter = true)
data class ComplianceResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "feedback") val feedback: String
)
