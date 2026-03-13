package com.dogukanpayal.victus_frontend.data.model

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)
