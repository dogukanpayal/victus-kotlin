package com.dogukanpayal.victus_frontend.data.remote

import com.dogukanpayal.victus_frontend.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface VictusApiService {
    @Multipart
    @POST("v1/nutrition/analyze")
    suspend fun analyzeNutrition(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<NutritionAnalysisResponse>

    @POST("v1/nutrition/analyze-text")
    suspend fun analyzeText(
        @Header("Authorization") token: String,
        @Body request: AnalyzeTextRequest
    ): Response<NutritionAnalysisResponse>

    @POST("v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("v1/user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>

    @GET("v1/user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<RegisterResponse>

    @POST("v1/nutrition/save")
    suspend fun saveNutrition(
        @Header("Authorization") token: String,
        @Body request: SaveNutritionRequest
    ): Response<Unit> // Başarı durumunda 201 Created veya 200 OK döner

    @GET("v1/nutrition/summary")
    suspend fun getSummary(
        @Header("Authorization") token: String
    ): Response<NutritionSummaryResponse>

    @GET("v1/nutrition/summary")
    suspend fun getDailySummary(
        @Header("Authorization") token: String
    ): Response<DailySummaryResponse>
}
