package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class AddWaterRequest(
    @Json(name = "amount_ml") val amountMl: Int = 250
)

data class AddWaterResponse(
    val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "amount_ml") val amountMl: Int,
    val date: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "daily_total_ml") val dailyTotalMl: Int
)

data class DailyWaterResponse(
    @Json(name = "user_id") val userId: String,
    val date: String,
    @Json(name = "total_ml") val totalMl: Int,
    @Json(name = "target_ml") val targetMl: Int,
    @Json(name = "entry_count") val entryCount: Int
)
