package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

/**
 * Maps to GET /v1/nutrition/meals response
 * { "user_id": "...", "date": "...", "meals": [...] }
 */
data class MealHistoryResponse(
    @Json(name = "user_id") val userId: String,
    @Json(name = "date") val date: String,
    @Json(name = "meals") val meals: List<MealHistoryItem> = emptyList()
)

data class MealHistoryItem(
    @Json(name = "id") val id: String,
    @Json(name = "food_name") val foodName: String,
    @Json(name = "calories") val calories: Double,
    @Json(name = "protein") val protein: Double,
    @Json(name = "carbs") val carbs: Double,
    @Json(name = "fat") val fat: Double,
    @Json(name = "portion") val portion: Double,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)
