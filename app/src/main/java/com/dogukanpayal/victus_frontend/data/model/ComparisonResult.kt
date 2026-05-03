package com.dogukanpayal.victus_frontend.data.model

data class ComparisonResult(
    val before: HealthMetricsData,
    val after: HealthMetricsData,
    val fatDelta: Double,
    val muscleDelta: Double,
    val weightDelta: Double,
    val bmiDelta: Double,
    val daysDifference: Int,
    val summaryText: String
)
