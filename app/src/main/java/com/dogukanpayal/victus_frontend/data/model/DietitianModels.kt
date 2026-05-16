package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class PatientSummary(
    val id: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String? = null,
    val lastActivity: String? = null,
    val goal: String? = null,
    val activePlanTitle: String? = null,
    val complianceRate: Int = 0 // Percentage 0-100
)

data class PatientDetail(
    val profile: ProfileResponse,
    val dailySummary: DailySummaryResponse? = null,
    val activePlan: ActivePlanResponse? = null,
    val activeDietPlanSummary: String? = null,
    val recentWorkoutSummary: String? = null,
    val complianceSummary: ComplianceSummary? = null
)

data class ComplianceSummary(
    @Json(name = "target_calories") val targetCalories: Double,
    @Json(name = "actual_calories") val actualCalories: Double,
    @Json(name = "target_protein") val targetProtein: Double,
    @Json(name = "actual_protein") val actualProtein: Double,
    @Json(name = "target_carbs") val targetCarbs: Double,
    @Json(name = "actual_carbs") val actualCarbs: Double,
    @Json(name = "target_fat") val targetFat: Double,
    @Json(name = "actual_fat") val actualFat: Double
)

data class DietitianPatientDetailResponse(
    val profile: ProfileResponse,
    @Json(name = "active_diet_plan_summary") val activeDietPlanSummary: String?,
    @Json(name = "recent_workout_summary") val recentWorkoutSummary: String?,
    @Json(name = "compliance_summary") val complianceSummary: ComplianceSummary?
)

data class PatientSummaryResponse(
    val id: String,
    val email: String,
    @Json(name = "full_name") val fullName: String,
    val goal: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "active_plan_title") val activePlanTitle: String?,
    @Json(name = "last_activity") val lastActivity: String?
)
