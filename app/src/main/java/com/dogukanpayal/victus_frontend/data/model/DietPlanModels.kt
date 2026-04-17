package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DietMeal(
    val name: String,
    val time: String, // "Kahvaltı", "Öğle", "Akşam", "Ara Öğün"
    val description: String,
    val calories: Int? = null
)

@JsonClass(generateAdapter = true)
data class DailyDietPlan(
    val dayIndex: Int, // 0 = Pazartesi ... 6 = Pazar
    val dayName: String,
    val meals: List<DietMeal>
)

@JsonClass(generateAdapter = true)
data class DietPlan(
    val id: String,
    val uploadedAt: Long,
    val sourceFileName: String,
    val days: List<DailyDietPlan> // Her zaman 7 gün
)
