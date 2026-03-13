package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.LoginRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<String>
}

class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(request: LoginRequest): Result<String> {
        // Mocking the backend call for now
        return Result.success("mock_token")
    }
}
