package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.remote.*

interface ProfileRepository {
    suspend fun updateProfile(
        accessToken: String,
        email: String,
        fullName: String,
        heightCm: Double,
        weightKg: Double,
        age: Int,
        sex: String,
        goal: String,
        kvkkConsentApproved: Boolean,
        avatarUrl: String?
    ): Result<ProfileResponse>

    suspend fun getProfile(
        accessToken: String
    ): Result<ProfileResponse>

    suspend fun linkDietitian(
        accessToken: String,
        code: String
    ): Result<Unit>
}

class ProfileRepositoryImpl(
    private val apiService: VictusApiService = RetrofitClient.apiService
) : ProfileRepository {
    override suspend fun updateProfile(
        accessToken: String,
        email: String,
        fullName: String,
        heightCm: Double,
        weightKg: Double,
        age: Int,
        sex: String,
        goal: String,
        kvkkConsentApproved: Boolean,
        avatarUrl: String?
    ): Result<ProfileResponse> {
        return try {
            val request = UpdateProfileRequest(
                email = email,
                fullName = fullName,
                heightCm = heightCm,
                weightKg = weightKg,
                age = age,
                sex = sex,
                goal = goal,
                kvkkConsentApproved = kvkkConsentApproved,
                avatarUrl = avatarUrl
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

    override suspend fun getProfile(
        accessToken: String
    ): Result<ProfileResponse> {
        return try {
            val authHeader = "Bearer $accessToken"
            val response = apiService.getProfile(authHeader)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Profile fetch failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun linkDietitian(
        accessToken: String,
        code: String
    ): Result<Unit> {
        return try {
            val request = LinkDietitianRequest(code = code)
            val authHeader = "Bearer $accessToken"
            val response = apiService.linkDietitian(authHeader, request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Bağlantı işlemi başarısız oldu."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
