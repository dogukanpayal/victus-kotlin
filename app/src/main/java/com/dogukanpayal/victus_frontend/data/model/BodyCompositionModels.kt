package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BodyCompositionReport(
    @Json(name = "current_metrics") val currentMetrics: HealthMetricsData,
    @Json(name = "fat_delta") val fatDelta: Double,
    @Json(name = "muscle_delta") val muscleDelta: Double,
    @Json(name = "posture_notes") val postureNotes: String,
    @Json(name = "confidence_score") val confidenceScore: Double
)

@JsonClass(generateAdapter = true)
data class HealthMetricsData(
    val id: String,
    @Json(name = "user_id") val userId: String,
    val weight: Double,
    @Json(name = "fat_mass_kg") val fatMassKg: Double,
    @Json(name = "fat_percentage") val fatPercentage: Double,
    @Json(name = "muscle_mass_kg") val muscleMassKg: Double,
    @Json(name = "muscle_percentage") val musclePercentage: Double,
    @Json(name = "water_percentage") val waterPercentage: Double,
    val bmi: Double,
    @Json(name = "posture_notes") val postureNotes: String,
    @Json(name = "confidence_score") val confidenceScore: Double,
    @Json(name = "is_ai_analysis") val isAiAnalysis: Boolean,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class AnalyzeBodyRequest(
    @Json(name = "base64_image") val base64Image: String,
    val weight: Double
)
