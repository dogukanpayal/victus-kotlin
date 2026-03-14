package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class UpdateProfileRequest(
    val email: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "weight_kg") val weightKg: Double,
    val age: Int,
    val sex: String,
    val goal: String
)


