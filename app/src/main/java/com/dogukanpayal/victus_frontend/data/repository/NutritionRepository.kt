package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dogukanpayal.victus_frontend.data.model.DailySummaryResponse
import com.dogukanpayal.victus_frontend.data.model.NutritionSummaryResponse
import com.dogukanpayal.victus_frontend.data.model.NutritionAnalysisResponse
import com.dogukanpayal.victus_frontend.data.model.SaveNutritionRequest
import com.dogukanpayal.victus_frontend.data.remote.VictusApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

interface NutritionRepository {
    suspend fun analyzeImage(token: String, imageUri: Uri, context: Context): Result<NutritionAnalysisResponse>
    suspend fun analyzeText(token: String, query: String, portion: Double): Result<NutritionAnalysisResponse>
    suspend fun saveMeal(token: String, request: SaveNutritionRequest): Result<Unit>
    suspend fun getSummary(token: String): Result<NutritionSummaryResponse>
    suspend fun getDailySummary(token: String): Result<DailySummaryResponse>
}

class NutritionRepositoryImpl(
    private val apiService: VictusApiService = com.dogukanpayal.victus_frontend.data.remote.RetrofitClient.apiService
) : NutritionRepository {

    companion object {
        private const val TAG = "NutritionRepository"
    }

    override suspend fun analyzeImage(token: String, imageUri: Uri, context: Context): Result<NutritionAnalysisResponse> {
        return try {
            val file = uriToFile(context, imageUri)
            val requestFile = file.asRequestBody("image/webp".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = apiService.analyzeNutrition("Bearer $token", body)
            
            // İşlem bitince geçici dosyayı silebiliriz
            if (file.exists()) file.delete()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Analizi yapılamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeText(token: String, query: String, portion: Double): Result<NutritionAnalysisResponse> {
        return try {
            val request = com.dogukanpayal.victus_frontend.data.model.AnalyzeTextRequest(query, portion)
            val response = apiService.analyzeText("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Yemek analiz edilemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveMeal(token: String, request: SaveNutritionRequest): Result<Unit> {
        return try {
            val response = apiService.saveNutrition("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Öğün kaydedilemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSummary(token: String): Result<NutritionSummaryResponse> {
        return try {
            val response = apiService.getSummary("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Özet alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailySummary(token: String): Result<DailySummaryResponse> {
        return try {
            val response = apiService.getDailySummary("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                val summary = response.body()!!
                Log.d(TAG, "getDailySummary: Başarılı")
                Log.d(TAG, "Response Body: $summary")
                Log.d(TAG, "Daily Goal: ${summary.dailyGoal}, Consumed: ${summary.caloriesConsumed}, Remaining: ${summary.caloriesRemaining}")
                Log.d(TAG, "Macros - Protein: ${summary.macros.proteinConsumed}/${summary.macros.proteinGoal}, " +
                    "Carbs: ${summary.macros.carbsConsumed}/${summary.macros.carbsGoal}, " +
                    "Fat: ${summary.macros.fatConsumed}/${summary.macros.fatGoal}")
                Result.success(summary)
            } else if (response.code() == 401) {
                Log.e(TAG, "getDailySummary: HTTP 401 Unauthorized")
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Log.e(TAG, "getDailySummary: Hata - ${response.message()}, Code: ${response.code()}")
                Result.failure(Exception("Günlük özet alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getDailySummary: Exception - ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.webp")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                FileOutputStream(tempFile).use { outputStream ->
                    val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        android.graphics.Bitmap.CompressFormat.WEBP_LOSSY
                    } else {
                        @Suppress("DEPRECATION")
                        android.graphics.Bitmap.CompressFormat.WEBP
                    }
                    bitmap.compress(format, 80, outputStream)
                }
            } else {
                throw Exception("Görsel parçalanamadı veya bozuk.")
            }
        } ?: throw Exception("Görsel dosyası okunamadı.")
        return tempFile
    }
}
