package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class ProfileResponse(
    val id: String,
    val email: String,
    val age: Int,
    val sex: String,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "weight_kg") val weightKg: Double,
    val bmr: Double,
    val goal: String,
    val daily_calories: Double? = null
)
