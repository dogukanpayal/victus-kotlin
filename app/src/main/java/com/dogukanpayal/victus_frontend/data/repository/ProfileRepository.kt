package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.remote.*

interface ProfileRepository {
    suspend fun updateProfile(
        accessToken: String,
        email: String,
        heightCm: Double,
        weightKg: Double,
        age: Int,
        sex: String
    ): Result<ProfileResponse>
}

class ProfileRepositoryImpl(
    private val apiService: VictusApiService = RetrofitClient.apiService
) : ProfileRepository {
    override suspend fun updateProfile(
        accessToken: String,
        email: String,
        heightCm: Double,
        weightKg: Double,
        age: Int,
        sex: String
    ): Result<ProfileResponse> {
        return try {
            val request = UpdateProfileRequest(
                email = email,
                heightCm = heightCm,
                weightKg = weightKg,
                age = age,
                sex = sex
            )
            // Backend expects "Bearer <token>"
            val authHeader = "Bearer $accessToken"
            val response = apiService.updateProfile(authHeader, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Profile update failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
