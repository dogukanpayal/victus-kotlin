package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json

data class RegisterResponse(
    val user: ProfileResponse,
    @Json(name = "access_token") val accessToken: String
)
