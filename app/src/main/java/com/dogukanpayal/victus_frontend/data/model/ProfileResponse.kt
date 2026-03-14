package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class ProfileResponse(
    val id: String,
    val email: String,
    @Json(name = "full_name") val fullName: String? = null,
    val age: Int,
    val sex: String,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "weight_kg") val weightKg: Double,
    val bmr: Double,
    val goal: String,
    @Json(name = "daily_calories") val dailyCalories: Double? = null
)
