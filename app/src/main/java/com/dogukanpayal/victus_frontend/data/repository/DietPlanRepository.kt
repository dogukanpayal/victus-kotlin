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
}
