package com.dogukanpayal.victus_frontend.data.model

data class FoodLogItem(
    val id: String,
    val foodName: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val imageUrl: String?,
    val createdAt: String // ISO-8601, UI'da "HH:mm" olarak gösterilecek
)
