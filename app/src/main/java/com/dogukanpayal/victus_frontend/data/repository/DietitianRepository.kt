package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.*
import kotlinx.coroutines.delay
import okhttp3.ResponseBody

import android.content.Context
import android.net.Uri

interface DietitianRepository {
    suspend fun getPatients(token: String): Result<List<PatientSummary>>
    suspend fun getPatientDetail(token: String, patientId: String): Result<PatientDetail>
    suspend fun downloadWeeklyReport(token: String, patientId: String): Result<ResponseBody>
    suspend fun uploadPatientDietPlan(
        context: Context,
        token: String,
        patientId: String,
        uri: Uri,
        mimeType: String
    ): Result<Unit>
}

class MockDietitianRepositoryImpl : DietitianRepository {
    override suspend fun getPatients(token: String): Result<List<PatientSummary>> {
        delay(1000) // Simulate network delay
        return Result.success(listOf(
            PatientSummary(
                id = "1",
                fullName = "Ahmet Yılmaz",
                email = "ahmet@example.com",
                lastActivity = "2 saat önce",
                goal = "Kilo Verme",
                activePlanTitle = "Haftalık Definasyon",
                complianceRate = 85
            ),
            PatientSummary(
                id = "2",
                fullName = "Ayşe Kaya",
                email = "ayse@example.com",
                lastActivity = "Dün",
                goal = "Kas Kazanımı",
                activePlanTitle = "Bulking v2",
                complianceRate = 92
            ),
            PatientSummary(
                id = "3",
                fullName = "Mehmet Demir",
                email = "mehmet@example.com",
                lastActivity = "15 dk önce",
                goal = "Form Koruma",
                activePlanTitle = "Stabil Yaşam",
                complianceRate = 64
            ),
            PatientSummary(
                id = "4",
                fullName = "Fatma Şahin",
                email = "fatma@example.com",
                lastActivity = "3 gün önce",
                goal = "Kilo Verme",
                activePlanTitle = "Ketojenik Başlangıç",
                complianceRate = 45
            )
        ))
    }

    override suspend fun getPatientDetail(token: String, patientId: String): Result<PatientDetail> {
        delay(800)
        // Mock detail for any patient ID
        val mockProfile = ProfileResponse(
            id = patientId,
            email = "patient@example.com",
            fullName = "Seçili Hasta",
            age = 28,
            sex = "male",
            heightCm = 180.0,
            weightKg = 75.0,
            bmr = 1800.0,
            goal = "LOSE_WEIGHT",
            role = "patient"
        )
        
        return Result.success(PatientDetail(
            profile = mockProfile,
            activeDietPlanSummary = "Haftalık Definasyon Planı (2100 kcal, %40 Protein)",
            recentWorkoutSummary = "Dün: 45 dk Kardiyo, 350 kcal yakıldı"
        ))
    }

    override suspend fun downloadWeeklyReport(token: String, patientId: String): Result<ResponseBody> {
        delay(500)
        return Result.success(ResponseBody.create(null, "Mock PDF Content"))
    }

    override suspend fun uploadPatientDietPlan(
        context: Context,
        token: String,
        patientId: String,
        uri: Uri,
        mimeType: String
    ): Result<Unit> {
        delay(800)
        return Result.success(Unit)
    }
}
