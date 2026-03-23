package com.dogukanpayal.victus_frontend.data.model

data class MealItem(
    val id: String,
    val name: String,
    val description: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val mealType: MealType,
    val portionSize: Float = 1.0f,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MealType(val displayName: String) {
    BREAKFAST("Kahvaltı"),
    LUNCH("Öğle Yemeği"),
    DINNER("Akşam Yemeği"),
    SNACK("Atıştırmalık")
}
