package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.remote.VictusApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
class DietitianRepositoryImpl(
    private val apiService: VictusApiService
) : DietitianRepository {

    override suspend fun getPatients(token: String): Result<List<PatientSummary>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDietitianPatients("Bearer $token")
            if (response.isSuccessful) {
                val profiles = response.body() ?: emptyList()
                val patients = profiles.map { profile ->
                    // Format date if needed, or use as is
                    PatientSummary(
                        id = profile.id,
                        fullName = profile.fullName,
                        email = profile.email,
                        avatarUrl = profile.avatarUrl,
                        goal = profile.goal,
                        lastActivity = profile.lastActivity,
                        lastAction = profile.lastAction,
                        activePlanTitle = profile.activePlanTitle ?: "Aktif plan yok",
                        complianceRate = profile.complianceRate ?: 0
                    )
                }
                Result.success(patients)
            } else {
                Result.failure(Exception("Hastalar getirilemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPatientDetail(token: String, patientId: String): Result<PatientDetail> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDietitianPatientDetail("Bearer $token", patientId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(PatientDetail(
                    profile = body.profile,
                    activeDietPlanSummary = body.activeDietPlanSummary,
                    recentWorkoutSummary = body.recentWorkoutSummary,
                    complianceSummary = body.complianceSummary,
                    waterSummary = body.waterSummary
                ))
            } else {
                Result.failure(Exception("Hasta detayı getirilemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadWeeklyReport(token: String, patientId: String): Result<ResponseBody> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.downloadWeeklyReport("Bearer $token", patientId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Rapor indirilemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPatientDietPlan(
        context: Context,
        token: String,
        patientId: String,
        uri: Uri,
        mimeType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.use { it.readBytes() } ?: throw Exception("Dosya okunamadı")

            val requestFile = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "diet_plan", requestFile)
            
            val startDatePart = java.time.LocalDate.now().toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val activatePart = "true".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadPatientDietPlan("Bearer $token", patientId, filePart, startDatePart, activatePart)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMsg = response.body()?.message ?: "Sunucu hatası: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
