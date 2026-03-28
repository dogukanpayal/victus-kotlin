package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class NutritionSummaryResponse(
    @Json(name = "target_calories") val targetCalories: Int,
    @Json(name = "remaining_calories") val remainingCalories: Int,
    @Json(name = "consumed_calories") val consumedCalories: Int? = null
)
