package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.remote.VictusApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
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
                    val formattedDate = profile.lastActivity?.let {
                        if (it.contains("T")) it.split("T")[0] else it
                    } ?: "Aktivite yok"

                    PatientSummary(
                        id = profile.id,
                        fullName = profile.fullName,
                        email = profile.email,
                        avatarUrl = profile.avatarUrl,
                        goal = profile.goal,
                        lastActivity = formattedDate,
                        activePlanTitle = profile.activePlanTitle ?: "Aktif plan yok",
                        complianceRate = 0 // Feature not yet implemented
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
                    complianceSummary = body.complianceSummary
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
}
