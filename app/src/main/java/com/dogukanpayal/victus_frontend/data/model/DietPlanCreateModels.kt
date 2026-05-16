package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

// ═══════════════════════════════════════════
// Network Models (Backend ile eşleşen)
// ═══════════════════════════════════════════

@JsonClass(generateAdapter = true)
data class CreateDietPlanRequest(
    val title: String,
    @Json(name = "duration_days") val durationDays: Int,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "start_date") val startDate: String, // YYYY-MM-DD
    val items: List<CreateDietPlanItemRequest>
)

@JsonClass(generateAdapter = true)
data class CreateDietPlanItemRequest(
    @Json(name = "day_number") val dayNumber: Int,
    @Json(name = "meal_index") val mealIndex: Int,
    @Json(name = "food_name") val foodName: String,
    @Json(name = "meal_type") val mealType: String,
    @Json(name = "target_calories") val targetCalories: Double = 0.0,
    @Json(name = "target_protein") val targetProtein: Double = 0.0,
    @Json(name = "target_carbs") val targetCarbs: Double = 0.0,
    @Json(name = "target_fat") val targetFat: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class DayTargetRequest(
    @Json(name = "day_number") val dayNumber: Int,       // 1-based
    @Json(name = "target_calories") val targetCalories: Double,
    @Json(name = "target_protein") val targetProtein: Double = 0.0,
    @Json(name = "target_carbs") val targetCarbs: Double = 0.0,
    @Json(name = "target_fat") val targetFat: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class CreateDietPlanResponse(
    @Json(name = "plan_id") val planId: String
)

// ═══════════════════════════════════════════
// UI Form Models (Ekranda kullanılan)
// ═══════════════════════════════════════════

// Her öğün girişi için form state
data class MealFormEntry(
    val id: String = UUID.randomUUID().toString(),
    val mealType: String = "Kahvaltı",  // Kahvaltı, Öğle, Akşam, Ara Öğün
    val description: String = "",
    val targetCalories: String = "",     // String (TextField'den gelecek)
    val targetProtein: String = "",
    val targetCarbs: String = "",
    val targetFat: String = ""
)

// Her gün için form state
data class DayFormState(
    val dayNumber: Int,   // 1-based (1, 2, 3, ...)
    val dayLabel: String, // "1. Gün", "2. Gün" ...
    val meals: List<MealFormEntry> = listOf(
        MealFormEntry(mealType = "Kahvaltı"),
        MealFormEntry(mealType = "Öğle"),
        MealFormEntry(mealType = "Akşam")
    )
)
