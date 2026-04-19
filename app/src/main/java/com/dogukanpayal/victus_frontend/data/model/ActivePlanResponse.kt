package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for fetching the full active diet plan
 * GET /v1/diet/active-plan
 */
@JsonClass(generateAdapter = true)
data class ActivePlanResponse(
    @Json(name = "plan")
    val plan: DietPlanData,
    
    @Json(name = "items")
    val items: List<DietPlanItemData>
)

@JsonClass(generateAdapter = true)
data class DietPlanData(
    val id: String,
    val title: String,
    @Json(name = "duration_days")
    val durationDays: Int,
    @Json(name = "start_date")
    val startDate: String
)

@JsonClass(generateAdapter = true)
data class DietPlanItemData(
    val id: String,
    @Json(name = "day_number")
    val dayNumber: Int,
    @Json(name = "meal_index")
    val mealIndex: Int,
    @Json(name = "food_name")
    val foodName: String,
    @Json(name = "meal_type")
    val mealType: String,
    @Json(name = "target_calories")
    val calories: Double,
    @Json(name = "target_protein")
    val protein: Double,
    @Json(name = "target_carbs")
    val carbs: Double,
    @Json(name = "target_fat")
    val fat: Double
)
