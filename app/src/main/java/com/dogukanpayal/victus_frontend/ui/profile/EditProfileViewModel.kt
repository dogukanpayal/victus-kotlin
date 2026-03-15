package com.dogukanpayal.victus_frontend.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.net.toUri
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepository
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepositoryImpl
import com.dogukanpayal.victus_frontend.data.storage.SupabaseStorageClient
import com.dogukanpayal.victus_frontend.ui.setup_profile.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "EditProfileViewModel"

enum class EditProfileUpdateState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

data class EditProfileState(
    val name: String = "",
    val email: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val age: Int = 0,
    val sex: String = "male",
    val fitnessGoal: Goal = Goal.LOSE_WEIGHT,
    val avatarUrl: String? = null,
    val selectedImageUri: String? = null,
    val isLoading: Boolean = true,
    val updateState: EditProfileUpdateState = EditProfileUpdateState.IDLE,
    val errorMessage: String = ""
)

@Suppress("StaticFieldLeak") // applicationContext kullanıldığından safe
class EditProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl(),
    private val context: Context? = null,
    private val supabaseStorageClient: SupabaseStorageClient = SupabaseStorageClient()
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    fun loadUserProfile(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                Log.d(TAG, "Profil düzenlenmek için yükleniyor...")
                
                val result = profileRepository.getProfile(accessToken)
                
                result.onSuccess { profileResponse ->
                    Log.d(TAG, "✅ Profil başarıyla yüklendi")
                    
                    val goalEnum = when (profileResponse.goal) {
                        "LOSE_WEIGHT" -> Goal.LOSE_WEIGHT
                        "GAIN_MUSCLE" -> Goal.GAIN_MUSCLE
                        "STAY_FIT" -> Goal.STAY_IN_SHAPE
                        else -> Goal.LOSE_WEIGHT
                    }
                    
                    val displayName = if (!profileResponse.fullName.isNullOrBlank()) {
                        profileResponse.fullName
                    } else {
                        ""
                    }
                    
                    _uiState.update {
                        it.copy(
                            name = displayName,
                            email = profileResponse.email,
                            heightCm = profileResponse.heightCm.toInt().toString(),
                            weightKg = profileResponse.weightKg.toInt().toString(),
                            age = profileResponse.age,
                            sex = profileResponse.sex,
                            fitnessGoal = goalEnum,
                            avatarUrl = profileResponse.avatarUrl,
                            isLoading = false,
                            updateState = EditProfileUpdateState.IDLE
                        )
                    }
                    
                    Log.d(TAG, "📝 Mevcut avatar: ${profileResponse.avatarUrl}")
                }
                
                result.onFailure { exception ->
                    Log.e(TAG, "❌ Profil yükleme başarısız: ${exception.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            updateState = EditProfileUpdateState.ERROR,
                            errorMessage = exception.message ?: "Profil yükleme başarısız"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Hata: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        updateState = EditProfileUpdateState.ERROR,
                        errorMessage = e.message ?: "Profil yükleme başarısız"
                    )
                }
            }
        }
    }

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onHeightChanged(newHeight: String) {
        _uiState.update { it.copy(heightCm = newHeight) }
    }

    fun onWeightChanged(newWeight: String) {
        _uiState.update { it.copy(weightKg = newWeight) }
    }

    fun onGoalChanged(newGoal: Goal) {
        _uiState.update { it.copy(fitnessGoal = newGoal) }
    }

    fun onPhotoSelected(imageUri: String) {
        Log.d(TAG, "📸 Yeni foto seçildi: $imageUri")
        _uiState.update { it.copy(selectedImageUri = imageUri) }
    }

    fun onSaveClicked(accessToken: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                
                // Validation
                if (currentState.name.isBlank() || currentState.heightCm.isBlank() || currentState.weightKg.isBlank()) {
                    _uiState.update {
                        it.copy(
                            updateState = EditProfileUpdateState.ERROR,
                            errorMessage = "Lütfen tüm alanları doldurunuz"
                        )
                    }
                    return@launch
                }
                
                _uiState.update { it.copy(updateState = EditProfileUpdateState.LOADING) }
                
                Log.d(TAG, "💾 Profil değişiklikleri kaydediliyor...")
                
                // STEP 1: Eğer yeni resim seçildiyse, Supabase Storage'a yükle
                var finalAvatarUrl = currentState.avatarUrl
                if (!currentState.selectedImageUri.isNullOrEmpty()) {
                    Log.d(TAG, "📸 Yeni resim seçildi. Upload işlemi başlıyor...")
                    
                    if (context != null) {
                        try {
                            val imageUri = currentState.selectedImageUri.toUri()
                            Log.d(TAG, "1️⃣ Content Provider'dan resim okunuyor: $imageUri")
                            
                            // Suspend function'u await et - upload tamamlanana kadar bekle
                            val uploadResult = supabaseStorageClient.uploadProfileImage(
                                context = context,
                                imageUri = imageUri
                            )
                            
                            // Sonuç başarılı mı kontrol et ve public URL'yi al
                            if (uploadResult.isSuccess) {
                                finalAvatarUrl = uploadResult.getOrNull()!!
                                Log.d(TAG, "✅ Supabase Storage upload başarılı!")
                                Log.d(TAG, "🌐 Public URL: $finalAvatarUrl")
                            } else {
                                Log.e(TAG, "❌ Supabase upload hatası: ${uploadResult.exceptionOrNull()?.message}")
                                // Fallback: URI'yi backend'e göndermeyi dene (ancak tavsiye edilmez)
                                finalAvatarUrl = currentState.selectedImageUri
                                Log.w(TAG, "⚠️ Fallback: Seçilen URI kullanılacak (internet erişilemez)")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Resim yükleme hatası: ${e.message}")
                            e.printStackTrace()
                            finalAvatarUrl = currentState.selectedImageUri
                        }
                    } else {
                        Log.w(TAG, "❌ Context null! Supabase upload yapılamadı.")
                        finalAvatarUrl = currentState.selectedImageUri
                    }
                } else {
                    Log.d(TAG, "📷 Yeni resim seçilmedi, mevcut avatar kullanılacak")
                }
                
                // STEP 2: Backend'e profil bilgilerini güncelle (public URL'yi kullanarak)
                Log.d(TAG, "2️⃣ Backend'e profil güncellemesi gönderiliyor...")
                
                val heightValue = currentState.heightCm.toDoubleOrNull() ?: 0.0
                val weightValue = currentState.weightKg.toDoubleOrNull() ?: 0.0
                val goalString = goalToString(currentState.fitnessGoal)
                
                Log.d(TAG, "📝 Gönderilen Veriler:")
                Log.d(TAG, "  • İsim: ${currentState.name}")
                Log.d(TAG, "  • E-posta: ${currentState.email}")
                Log.d(TAG, "  • Boy: $heightValue cm")
                Log.d(TAG, "  • Kilo: $weightValue kg")
                Log.d(TAG, "  • Hedef: $goalString")
                Log.d(TAG, "  • Avatar URL: $finalAvatarUrl")
                
                val result = profileRepository.updateProfile(
                    accessToken = accessToken,
                    email = currentState.email,
                    fullName = currentState.name,
                    heightCm = heightValue,
                    weightKg = weightValue,
                    age = currentState.age,
                    sex = currentState.sex,
                    goal = goalString,
                    avatarUrl = finalAvatarUrl
                )
                
                result.onSuccess { profileResponse ->
                    Log.d(TAG, "✅ Profil başarıyla güncellendi!")
                    Log.d(TAG, "🎉 Backend'den dönen avatar: ${profileResponse.avatarUrl}")
                    _uiState.update {
                        it.copy(
                            updateState = EditProfileUpdateState.SUCCESS,
                            errorMessage = "",
                            avatarUrl = profileResponse.avatarUrl,
                            selectedImageUri = null // Temizle
                        )
                    }
                    onSuccess()
                }
                
                result.onFailure { exception ->
                    Log.e(TAG, "❌ Profil güncellemesi başarısız: ${exception.message}")
                    _uiState.update {
                        it.copy(
                            updateState = EditProfileUpdateState.ERROR,
                            errorMessage = exception.message ?: "Profil güncellenirken hata oluştu"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Beklenmeyen hata: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        updateState = EditProfileUpdateState.ERROR,
                        errorMessage = e.message ?: "Profil güncellenirken hata oluştu"
                    )
                }
            }
        }
    }

    private fun goalToString(goal: Goal): String {
        return when (goal) {
            Goal.LOSE_WEIGHT -> "LOSE_WEIGHT"
            Goal.GAIN_MUSCLE -> "GAIN_MUSCLE"
            Goal.STAY_IN_SHAPE -> "STAY_FIT"
        }
    }
}

