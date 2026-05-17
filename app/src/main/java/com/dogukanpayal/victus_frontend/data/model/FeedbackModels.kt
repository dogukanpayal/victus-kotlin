package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class FeedbackMessage(
    @Json(name = "id") val id: String,
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "sender_type") val senderType: String, // "ai", "dietitian"
    @Json(name = "sender_id") val senderId: String?,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "category") val category: String, // "nutrition", "progress", "general"
    @Json(name = "is_read") val isRead: Boolean,
    @Json(name = "created_at") val createdAt: String
)

data class SendFeedbackRequest(
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "category") val category: String = "general"
)
