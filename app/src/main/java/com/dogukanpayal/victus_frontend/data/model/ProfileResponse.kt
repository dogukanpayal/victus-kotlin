package com.dogukanpayal.victus_frontend.data.model

data class ProfileResponse(
    val id: String,
    val email: String,
    val age: Int,
    val sex: String,
    val heightCm: Double,
    val weightKg: Double,
    val bmr: Double
)
