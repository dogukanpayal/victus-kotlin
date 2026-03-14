package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class RegisterRequest(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)
