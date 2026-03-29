package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

/**
 * Multi-item nutrition analysis response from backend.
 */
data class NutritionAnalysisResponse(
    @Json(name = "results") val results: List<NutritionAnalysisItem>,
    @Json(name = "image_url") val imageUrl: String? = null
)

/**
 * Individual food item detected in a single photo.
 */
data class NutritionAnalysisItem(
    @Json(name = "food_name") val foodName: String,
    @Json(name = "calories") val calories: Int,
    @Json(name = "protein") val protein: Float,
    @Json(name = "carbs") val carbs: Float,
    @Json(name = "fat") val fat: Float,
    @Json(name = "portion") val portionSize: Float,
    @Json(name = "label_en") val labelEn: String? = null
)
