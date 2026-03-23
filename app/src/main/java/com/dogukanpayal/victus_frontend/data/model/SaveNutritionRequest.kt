package com.dogukanpayal.victus_frontend.data.model

data class SaveNutritionRequest(
    val foodName: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val portion: Float
)
