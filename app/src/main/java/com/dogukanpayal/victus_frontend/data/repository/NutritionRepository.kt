package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.net.Uri
import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.remote.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

interface NutritionRepository {
    suspend fun analyzeImage(
        accessToken: String,
        imageUri: Uri,
        context: Context
    ): Result<NutritionAnalysisResponse>
}

class NutritionRepositoryImpl(
    private val apiService: VictusApiService = RetrofitClient.apiService
) : NutritionRepository {

    override suspend fun analyzeImage(
        accessToken: String,
        imageUri: Uri,
        context: Context
    ): Result<NutritionAnalysisResponse> {
        return try {
            val file = uriToFile(context, imageUri) ?: return Result.failure(Exception("Dosya oluşturulamadı"))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val authHeader = "Bearer $accessToken"
            val response = apiService.analyzeNutrition(authHeader, body)

            if (response.isSuccessful && response.body() != null) {
                // Analiz sonrası geçici dosyayı temizle
                file.delete()
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Analiz başarısız oldu"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uri'yi geçici bir File nesnesine dönüştürür.
     * Retrofit Multipart isteği için dosya yolu gereklidir.
     */
    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        
        try {
            FileOutputStream(tempFile).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }
            return tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
