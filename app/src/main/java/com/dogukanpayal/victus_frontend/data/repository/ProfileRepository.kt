package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.ProfileResponse

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

class ProfileRepositoryImpl : ProfileRepository {
    override suspend fun updateProfile(
        accessToken: String,
        email: String,
        heightCm: Double,
        weightKg: Double,
        age: Int,
        sex: String
    ): Result<ProfileResponse> {
        return try {
            // Mock implementation - In production, this would call the backend API
            val response = ProfileResponse(
                id = "mock_user_id",
                email = email,
                age = age,
                sex = sex,
                heightCm = heightCm,
                weightKg = weightKg,
                bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + (if (sex == "male") 5.0 else -161.0)
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
