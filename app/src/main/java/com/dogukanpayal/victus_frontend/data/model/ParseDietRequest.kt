package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request for parsing and saving a diet plan via AI
 * POST /v1/diet/parse-and-save
 */
@JsonClass(generateAdapter = true)
data class ParseDietRequest(
    @Json(name = "raw_text")
    val rawText: String,
    
    @Json(name = "start_date")
    val startDate: String, // format: YYYY-MM-DD
    
    @Json(name = "activate")
    val activate: Boolean = true
)

/**
 * Response for diet parsing
 */
@JsonClass(generateAdapter = true)
data class ParseDietResponse(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "message")
    val message: String? = null,
    
    @Json(name = "diet_plan_id")
    val dietPlanId: String? = null
)
