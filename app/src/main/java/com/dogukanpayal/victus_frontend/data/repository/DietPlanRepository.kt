package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.net.Uri
import com.dogukanpayal.victus_frontend.data.model.DietPlan

interface DietPlanRepository {
    /**
     * Diyet listesini (PDF veya görsel) verip 7 günlük plan döner.
     * Şimdilik backend olmadığı için yerel bir mock plan döndürüyor.
     */
    suspend fun analyzeDietPlan(
        context: Context,
        uri: Uri,
        mimeType: String,
        token: String
    ): Result<DietPlan>

    suspend fun getSavedPlan(): DietPlan?
    suspend fun savePlan(plan: DietPlan)
    suspend fun clearPlan()

    /**
     * Günlük öğünleri hedefle kıyaslayarak AI destekli geri bildirim alır.
     */
    suspend fun analyzeDietCompliance(
        token: String,
        meals: List<com.dogukanpayal.victus_frontend.data.model.MealItem>,
        target: com.dogukanpayal.victus_frontend.data.model.DailyDietPlan
    ): Result<String>
}
