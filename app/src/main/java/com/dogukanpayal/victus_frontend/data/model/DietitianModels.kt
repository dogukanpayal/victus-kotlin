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
    val recentWorkoutSummary: String? = null
)

data class DietitianPatientDetailResponse(
    val profile: ProfileResponse,
    @Json(name = "active_diet_plan_summary") val activeDietPlanSummary: String?,
    @Json(name = "recent_workout_summary") val recentWorkoutSummary: String?
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
