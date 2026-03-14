package com.dogukanpayal.victus_frontend.data.model

data class AuthSession(
    val accessToken: String,
    val userId: String,
    val email: String
)
