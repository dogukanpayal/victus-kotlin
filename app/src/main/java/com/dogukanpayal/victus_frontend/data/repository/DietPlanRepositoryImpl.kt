package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import android.net.Uri
import com.dogukanpayal.victus_frontend.data.model.DailyDietPlan
import com.dogukanpayal.victus_frontend.data.model.DietMeal
import com.dogukanpayal.victus_frontend.data.model.DietPlan
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

class DietPlanRepositoryImpl(private val context: Context) : DietPlanRepository {

    private val prefs = context.getSharedPreferences("diet_plan_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(DietPlan::class.java)

    override suspend fun analyzeDietPlan(
        context: Context,
        uri: Uri,
        mimeType: String,
        token: String
    ): Result<DietPlan> = withContext(Dispatchers.IO) {
        try {
            // İleride buraya ağ isteği (POST /v1/diet-plan/analyze) gelecek
            
            // Simüle edilmiş bekleme süresi (Yapay zeka analiz ediyormuş gibi)
            delay(2000)

            val mockPlan = generateMockPlan()
            savePlan(mockPlan) // Otomatik olarak yerel state'e kaydet
            Result.success(mockPlan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSavedPlan(): DietPlan? = withContext(Dispatchers.IO) {
        val json = prefs.getString("saved_diet_plan", null)
        if (json.isNullOrBlank()) return@withContext null
        try {
            adapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun savePlan(plan: DietPlan) = withContext(Dispatchers.IO) {
        val json = adapter.toJson(plan)
        prefs.edit().putString("saved_diet_plan", json).apply()
    }

    override suspend fun clearPlan() = withContext(Dispatchers.IO) {
        prefs.edit().remove("saved_diet_plan").apply()
    }

    private fun generateMockPlan(): DietPlan {
        val daysOfWeek = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar")
        
        val days = daysOfWeek.mapIndexed { index, dayName ->
            DailyDietPlan(
                dayIndex = index,
                dayName = dayName,
                meals = listOf(
                    DietMeal(
                        name = "Yulaf Lapası",
                        time = "Kahvaltı - 08:30",
                        description = "3 kaşık yulaf, 1 su bardağı süt, yarım muz, 5 adet badem.",
                        calories = 320
                    ),
                    DietMeal(
                        name = "Yeşil Çay & Ceviz",
                        time = "Ara Öğün - 11:00",
                        description = "1 fincan şekersiz yeşil çay, 2 tam ceviz.",
                        calories = 100
                    ),
                    DietMeal(
                        name = "Izgara Tavuk Salata",
                        time = "Öğle - 13:30",
                        description = "150g ızgara tavuk göğsü, bol yeşillik, 1 tatlı kaşığı zeytinyağı.",
                        calories = 350
                    ),
                    DietMeal(
                        name = "Meyve & Yoğurt",
                        time = "Ara Öğün - 16:00",
                        description = "1 kase ev yoğurdu, 1 porsiyon mevsim meyvesi.",
                        calories = 150
                    ),
                    DietMeal(
                        name = "Fırın Somon",
                        time = "Akşam - 19:30",
                        description = "200g fırın somon, buharda brokoli ve kuşkonmaz.",
                        calories = 400
                    )
                )
            )
        }

        return DietPlan(
            id = UUID.randomUUID().toString(),
            uploadedAt = System.currentTimeMillis(),
            sourceFileName = "Örnek Diyet Listesi.pdf",
            days = days
        )
    }
}
