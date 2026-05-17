package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.AddWaterRequest
import com.dogukanpayal.victus_frontend.data.model.AddWaterResponse
import com.dogukanpayal.victus_frontend.data.model.DailyWaterResponse
import com.dogukanpayal.victus_frontend.data.remote.RetrofitClient
import com.dogukanpayal.victus_frontend.data.remote.VictusApiService

interface WaterRepository {
    suspend fun addWater(token: String, amountMl: Int = 250): Result<AddWaterResponse>
    suspend fun getDailyWater(token: String, date: String? = null): Result<DailyWaterResponse>
}

class WaterRepositoryImpl(
    private val apiService: VictusApiService = RetrofitClient.apiService
) : WaterRepository {
    override suspend fun addWater(token: String, amountMl: Int): Result<AddWaterResponse> {
        return try {
            val request = AddWaterRequest(amountMl)
            val response = apiService.addWaterIntake("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Su eklenemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyWater(token: String, date: String?): Result<DailyWaterResponse> {
        return try {
            val response = apiService.getDailyWater("Bearer $token", date)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Su bilgisi alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
