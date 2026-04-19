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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
            // 1. Uri'den ByteArray'e dönüştür
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.use { it.readBytes() } ?: throw Exception("Dosya okunamadı")

            // 2. MultipartBody.Part hazırla
            val requestFile = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "diet_plan", requestFile)
            
            val startDatePart = java.time.LocalDate.now().toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val activatePart = "true".toRequestBody("text/plain".toMediaTypeOrNull())

            // 3. API çağrısı yap
            val service = com.dogukanpayal.victus_frontend.data.remote.RetrofitClient.apiService
            val response = service.uploadDietPlan("Bearer $token", filePart, startDatePart, activatePart)

            if (response.isSuccessful && response.body()?.success == true) {
                // Başarılı ise backend zaten planı kaydetti ve aktif etti.
                Result.success(generateEmptySuccessPlan())
            } else {
                val errorMsg = response.body()?.message ?: "Sunucu hatası: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchActivePlan(token: String): Result<DietPlan?> = withContext(Dispatchers.IO) {
        try {
            val service = com.dogukanpayal.victus_frontend.data.remote.RetrofitClient.apiService
            val response = service.getActivePlan("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                
                // Backend'den gelen düz listeyi günlere göre grupla
                val mealsByDay = body.items.groupBy { it.dayNumber }
                
                val days = mealsByDay.keys.sorted().map { dayNum ->
                    val meals = mealsByDay[dayNum]!!.map { item ->
                        com.dogukanpayal.victus_frontend.data.model.DietMeal(
                            name = item.foodName,
                            time = item.mealType,
                            description = "Porsiyon bilgisi: ${item.mealType}",
                            calories = item.calories.toInt()
                        )
                    }
                    
                    com.dogukanpayal.victus_frontend.data.model.DailyDietPlan(
                        dayIndex = dayNum - 1,
                        dayName = "Gün $dayNum",
                        meals = meals
                    )
                }

                val dietPlan = DietPlan(
                    id = body.plan.id,
                    uploadedAt = System.currentTimeMillis(), // Şimdilik yerel zaman
                    sourceFileName = body.plan.title,
                    days = days
                )
                
                Result.success(dietPlan)
            } else if (response.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(Exception("Aktif plan alınamadı: ${response.code()}"))
            }
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
    private fun generateEmptySuccessPlan(): DietPlan {
        return DietPlan(
            id = UUID.randomUUID().toString(),
            uploadedAt = System.currentTimeMillis(),
            sourceFileName = "Analiz Başarılı",
            days = emptyList()
        )
    }

    override suspend fun analyzeDietCompliance(
        token: String,
        meals: List<com.dogukanpayal.victus_frontend.data.model.MealItem>,
        target: DailyDietPlan
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Plan total calories target
            val targetCalories = target.meals.mapNotNull { it.calories }.sum().toDouble()
            // Assume default macro ratios if explicitly not provided, just as mock
            val targetDTO = com.dogukanpayal.victus_frontend.data.model.DietTargetDTO(
                targetCalories = if (targetCalories > 0) targetCalories else 2000.0,
                targetProtein = 150.0,
                targetCarbs = 200.0,
                targetFat = 60.0
            )

            val consumedMeals = meals.map {
                com.dogukanpayal.victus_frontend.data.model.ConsumedMealDTO(
                    name = it.name,
                    calories = it.calories.toDouble(),
                    protein = it.protein.toDouble(),
                    carbs = it.carbs.toDouble(),
                    fat = it.fat.toDouble()
                )
            }

            val request = com.dogukanpayal.victus_frontend.data.model.ComplianceRequest(
                consumedMeals = consumedMeals,
                dailyTarget = targetDTO
            )

            val service = com.dogukanpayal.victus_frontend.data.remote.RetrofitClient.apiService
            val response = service.analyzeDietCompliance("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.feedback)
            } else {
                Result.failure(Exception("Analiz alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
