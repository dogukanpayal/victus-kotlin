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
