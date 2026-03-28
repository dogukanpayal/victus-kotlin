package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class SaveNutritionRequest(
    @Json(name = "food_name") val foodName: String,
    @Json(name = "calories") val calories: Int,
    @Json(name = "protein") val protein: Float,
    @Json(name = "carbs") val carbs: Float,
    @Json(name = "fat") val fat: Float,
    @Json(name = "portion") val portion: Float,
    @Json(name = "image_url") val imageUrl: String? = null
)
