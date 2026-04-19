package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.dogukanpayal.victus_frontend.data.model.DailySummaryResponse
import com.dogukanpayal.victus_frontend.data.model.FoodLogItem
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
    suspend fun getDailySummary(token: String, date: String? = null): Result<DailySummaryResponse>
    suspend fun getMealHistory(token: String, date: String): Result<List<FoodLogItem>>
    suspend fun parseAndSaveDiet(token: String, rawText: String, startDate: String, activate: Boolean): Result<com.dogukanpayal.victus_frontend.data.model.ParseDietResponse>
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

    override suspend fun getDailySummary(token: String, date: String?): Result<DailySummaryResponse> {
        return try {
            val response = apiService.getDailySummary("Bearer $token", date)
            
            if (response.isSuccessful && response.body() != null) {
                val summary = response.body()!!
                Log.d(TAG, "getDailySummary: Başarılı")
                Log.d(TAG, "Response Body: $summary")
                Log.d(TAG, "Daily Goal: ${summary.dailyGoal}, Consumed: ${summary.caloriesConsumed}, Remaining: ${summary.caloriesRemaining}")
                Log.d(TAG, "Targets - Protein: ${summary.macros.targets.protein}, " +
                    "Carbs: ${summary.macros.targets.carbs}, " +
                    "Fat: ${summary.macros.targets.fat}")
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

    override suspend fun getMealHistory(token: String, date: String): Result<List<FoodLogItem>> {
        return try {
            val response = apiService.getMealHistory("Bearer $token", date)
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.meals.map { item ->
                    FoodLogItem(
                        id = item.id,
                        foodName = item.foodName,
                        calories = item.calories.toInt(),
                        protein = item.protein.toFloat(),
                        carbs = item.carbs.toFloat(),
                        fat = item.fat.toFloat(),
                        imageUrl = item.imageUrl,
                        createdAt = item.createdAt ?: ""
                    )
                }
                Result.success(items)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Öğün geçmişi alınamadı: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun parseAndSaveDiet(
        token: String,
        rawText: String,
        startDate: String,
        activate: Boolean
    ): Result<com.dogukanpayal.victus_frontend.data.model.ParseDietResponse> {
        return try {
            val request = com.dogukanpayal.victus_frontend.data.model.ParseDietRequest(rawText, startDate, activate)
            val response = apiService.parseAndSaveDiet("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                Result.failure(Exception("HTTP 401 Unauthorized"))
            } else {
                Result.failure(Exception("Diyet planı işlenemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.webp")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                ?: throw Exception("Görsel parçalanamadı veya bozuk.")

            // EXIF orientation oku → bitmap'i döndür
            val rotatedBitmap = context.contentResolver.openInputStream(uri)?.use { exifStream ->
                val exif = ExifInterface(exifStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val rotation = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90  -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
                if (rotation != 0f) {
                    val matrix = Matrix().apply { postRotate(rotation) }
                    android.graphics.Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    ).also { if (it != bitmap) bitmap.recycle() }
                } else {
                    bitmap
                }
            } ?: bitmap

            FileOutputStream(tempFile).use { outputStream ->
                val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    android.graphics.Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    android.graphics.Bitmap.CompressFormat.WEBP
                }
                rotatedBitmap.compress(format, 80, outputStream)
            }
        } ?: throw Exception("Görsel dosyası okunamadı.")

        return tempFile
    }
}
