package com.dogukanpayal.victus_frontend.data.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dogukanpayal.victus_frontend.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.util.UUID

private const val TAG = "SupabaseStorageClient"

class SupabaseStorageClient(
    private val supabaseUrl: String = BuildConfig.SUPABASE_URL,
    private val supabaseKey: String = BuildConfig.SUPABASE_KEY
) {
    private val client = OkHttpClient()
    private val bucketName = "avatars"

    /**
     * Supabase Storage'a resim yükleme
     * @param context Android Context (ContentResolver için)
     * @param imageUri Seçilen resmin Content URI'si
     * @return Yüklenen dosyanın public URL'si veya hata
     */
    suspend fun uploadProfileImage(
        context: Context,
        imageUri: Uri
    ): Result<String> {
        return try {
            Log.d(TAG, "📸 Profil resmi yükleme başlıyor...")
            Log.d(TAG, "📍 URI: $imageUri")
            Log.d(TAG, "🧵 Thread: ${Thread.currentThread().name}")
            
            // STEP 1: Content Provider'dan file okuma (Main Thread'de uygun)
            Log.d(TAG, "1️⃣ Content Provider'dan dosya okunuyor...")
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("Dosya açılamadı - URI geçersiz olabilir"))

            // Dosyayı byte array'e dönüştür
            val fileBytes = inputStream.use { it.readBytes() }
            Log.d(TAG, "✅ Dosya başarıyla okundu! Boyut: ${fileBytes.size} bytes")

            // STEP 2: Dosya adlandırması ve path oluşturma
            val fileName = "user_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val uploadPath = "$bucketName/$fileName"
            Log.d(TAG, "2️⃣ Dosya adı oluşturuldu: $fileName")

            // STEP 3: Supabase Storage REST API endpoint
            val uploadUrl = "$supabaseUrl/storage/v1/object/$bucketName/$fileName"
            Log.d(TAG, "3️⃣ Upload URL: $uploadUrl")
            Log.d(TAG, "🔐 Supabase Key: ${supabaseKey.take(20)}... (gizlendi)")

            // STEP 4: ByteArray'dan RequestBody oluştur
            val requestBody = fileBytes.toRequestBody("image/jpeg".toMediaType())
            Log.d(TAG, "4️⃣ RequestBody oluşturuldu (image/jpeg)")

            // STEP 5: HTTP isteğini oluştur
            // Supabase API key'ini temizle (trim) ve header'ları hazırla
            val cleanApiKey = supabaseKey.trim()
            
            Log.d(TAG, "5️⃣ HTTP POST isteği hazırlanıyor...")
            Log.d(TAG, "📤 Authorization Header: Bearer ${cleanApiKey.take(20)}... (gizlendi)")
            Log.d(TAG, "🔑 apikey Header: ${cleanApiKey.take(20)}... (gizlendi)")
            
            val request = Request.Builder()
                .url(uploadUrl)
                .addHeader("Authorization", "Bearer $cleanApiKey")
                .addHeader("apikey", cleanApiKey)
                .addHeader("Content-Type", "image/jpeg")
                .post(requestBody)
                .build()
            Log.d(TAG, "✅ HTTP POST isteği hazırlandı (2 header ile)")

            // STEP 6 & 7: Ağ isteğini IO Thread'de yap
            Log.d(TAG, "🔄 IO Thread'e geçiliyor...")
            val result = withContext(Dispatchers.IO) {
                Log.d(TAG, "6️⃣ Supabase Storage'a dosya gönderiliyor...")
                Log.d(TAG, "🧵 Network Thread: ${Thread.currentThread().name}")
                
                val response = client.newCall(request).execute()
                Log.d(TAG, "📡 Response alındı - HTTP Status: ${response.code}")
                Log.d(TAG, "🧵 Response Thread: ${Thread.currentThread().name}")

                if (response.isSuccessful) {
                    // STEP 7: Public URL oluştur
                    val publicUrl = "$supabaseUrl/storage/v1/object/public/$uploadPath"
                    Log.d(TAG, "7️⃣ Upload başarılı! ✅")
                    Log.d(TAG, "🌐 Public URL oluşturuldu:")
                    Log.d(TAG, "   $publicUrl")
                    Result.success(publicUrl)
                } else {
                    val errorBody = response.body?.string() ?: "Bilinmeyen hata"
                    Log.e(TAG, "❌ Upload başarısız!")
                    Log.e(TAG, "HTTP Status: ${response.code}")
                    Log.e(TAG, "Error Body: $errorBody")
                    Result.failure(Exception("Upload başarısız: HTTP ${response.code}"))
                }
            }
            
            Log.d(TAG, "✅ Network işlemi tamamlandı")
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ Upload işleminde exception oluştu!")
            Log.e(TAG, "Hata türü: ${e::class.simpleName}")
            Log.e(TAG, "Hata mesajı: ${e.message}")
            Log.e(TAG, "🧵 Exception Thread: ${Thread.currentThread().name}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}



