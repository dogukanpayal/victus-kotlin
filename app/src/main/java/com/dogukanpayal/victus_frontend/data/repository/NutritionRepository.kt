package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.net.Uri
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
    suspend fun saveMeal(token: String, request: SaveNutritionRequest): Result<Unit>
}

class NutritionRepositoryImpl(
    private val apiService: VictusApiService = com.dogukanpayal.victus_frontend.data.remote.RetrofitClient.apiService
) : NutritionRepository {

    override suspend fun analyzeImage(token: String, imageUri: Uri, context: Context): Result<NutritionAnalysisResponse> {
        return try {
            val file = uriToFile(context, imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = apiService.analyzeNutrition("Bearer $token", body)
            
            // İşlem bitince geçici dosyayı silebiliriz
            if (file.exists()) file.delete()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Analizi yapılamadı: ${response.message()}"))
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
            } else {
                Result.failure(Exception("Öğün kaydedilemedi: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
