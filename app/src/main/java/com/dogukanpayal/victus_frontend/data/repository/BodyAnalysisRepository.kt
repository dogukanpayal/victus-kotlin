package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.remote.RetrofitClient
import com.dogukanpayal.victus_frontend.data.remote.VictusApiService
import java.io.ByteArrayOutputStream
import java.io.InputStream

interface BodyAnalysisRepository {
    suspend fun analyzeBody(token: String, imageUri: Uri, context: Context, weight: Double): Result<BodyCompositionReport>
    suspend fun getMetricsHistory(token: String): Result<List<HealthMetricsData>>
    suspend fun deleteMetrics(token: String, id: String): Result<Unit>
    suspend fun analyzeProgress(token: String, request: ProgressAnalysisRequest): Result<ProgressAnalysisResponse>
}

class BodyAnalysisRepositoryImpl(
    private val apiService: VictusApiService = RetrofitClient.apiService
) : BodyAnalysisRepository {

    override suspend fun analyzeBody(token: String, imageUri: Uri, context: Context, weight: Double): Result<BodyCompositionReport> {
        return try {
            val base64Image = uriToBase64(context, imageUri)
            val request = AnalyzeBodyRequest(base64Image, weight)
            val response = apiService.analyzeBodyComposition("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Analiz başarısız: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMetricsHistory(token: String): Result<List<HealthMetricsData>> {
        return try {
            val response = apiService.getMetricsHistory("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Geçmiş yüklenemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMetrics(token: String, id: String): Result<Unit> {
        return try {
            val response = apiService.deleteMetrics("Bearer $token", id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Silme başarısız: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeProgress(token: String, request: ProgressAnalysisRequest): Result<ProgressAnalysisResponse> {
        return try {
            val response = apiService.analyzeProgress("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gelişim analizi başarısız: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        
        // Resize if too large to avoid memory issues and speed up upload
        val scaledBitmap = if (bitmap.width > 1200 || bitmap.height > 1200) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            if (bitmap.width > bitmap.height) {
                Bitmap.createScaledBitmap(bitmap, 1200, (1200 / ratio).toInt(), true)
            } else {
                Bitmap.createScaledBitmap(bitmap, (1200 * ratio).toInt(), 1200, true)
            }
        } else {
            bitmap
        }

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
