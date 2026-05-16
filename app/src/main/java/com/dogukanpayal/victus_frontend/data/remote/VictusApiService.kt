package com.dogukanpayal.victus_frontend.data.remote

import com.dogukanpayal.victus_frontend.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    suspend fun getDailySummary(
        @Header("Authorization") token: String,
        @Query("date") date: String? = null
    ): Response<DailySummaryResponse>

    @POST("v1/diet/parse-and-save")
    suspend fun parseAndSaveDiet(
        @Header("Authorization") token: String,
        @Body request: ParseDietRequest
    ): Response<ParseDietResponse>

    @GET("v1/nutrition/meals")
    suspend fun getMealHistory(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<MealHistoryResponse>

    @DELETE("v1/nutrition/meal/{id}")
    suspend fun deleteMeal(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @POST("v1/nutrition/analyze-compliance")
    suspend fun analyzeDietCompliance(
        @Header("Authorization") token: String,
        @Body request: ComplianceRequest
    ): Response<ComplianceResponse>

    @GET("v1/diet/active-plan")
    suspend fun getActivePlan(
        @Header("Authorization") token: String
    ): Response<ActivePlanResponse>

    /**
     * Diyet listesi dosyası (PDF/Görsel) yükler
     */
    @Multipart
    @POST("v1/diet/upload")
    suspend fun uploadDietPlan(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("start_date") startDate: RequestBody,
        @Part("activate") activate: RequestBody
    ): Response<ParseDietResponse>

    @GET("v1/workout/exercises")
    suspend fun getExercises(
        @Header("Authorization") token: String
    ): Response<List<Exercise>>

    @POST("v1/workout/log")
    suspend fun logWorkout(
        @Header("Authorization") token: String,
        @Body request: WorkoutLogRequest
    ): Response<WorkoutLogResponse>

    @DELETE("v1/workout/log/{id}")
    suspend fun deleteWorkoutLog(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @DELETE("v1/workout/logs/reset")
    suspend fun resetWorkoutLogs(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("v1/user/metrics/analyze")
    suspend fun analyzeBodyComposition(
        @Header("Authorization") token: String,
        @Body request: AnalyzeBodyRequest
    ): Response<BodyCompositionReport>

    @GET("v1/user/metrics")
    suspend fun getMetricsHistory(
        @Header("Authorization") token: String
    ): Response<List<HealthMetricsData>>

    @DELETE("v1/user/metrics/{id}")
    suspend fun deleteMetrics(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @POST("v1/user/metrics/analyze-progress")
    suspend fun analyzeProgress(
        @Header("Authorization") token: String,
        @Body request: ProgressAnalysisRequest
    ): Response<ProgressAnalysisResponse>

    @POST("v1/diet/plans")
    suspend fun createDietPlan(
        @Header("Authorization") token: String,
        @Body request: CreateDietPlanRequest
    ): Response<CreateDietPlanResponse>

    @GET("v1/dietitian/patients")
    suspend fun getDietitianPatients(
        @Header("Authorization") token: String
    ): Response<List<PatientSummaryResponse>>

    @GET("v1/dietitian/patients/{id}")
    suspend fun getDietitianPatientDetail(
        @Header("Authorization") token: String,
        @Path("id") patientId: String
    ): Response<DietitianPatientDetailResponse>
}
