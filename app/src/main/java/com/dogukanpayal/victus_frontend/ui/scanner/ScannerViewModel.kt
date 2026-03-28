package com.dogukanpayal.victus_frontend.ui.scanner

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.SaveNutritionRequest
import com.dogukanpayal.victus_frontend.data.repository.NutritionRepository
import com.dogukanpayal.victus_frontend.data.repository.NutritionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

class ScannerViewModel(
    private val repository: NutritionRepository = NutritionRepositoryImpl()
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _lastScanResult = MutableStateFlow<LastScanResult?>(null)
    val lastScanResult: StateFlow<LastScanResult?> = _lastScanResult.asStateFlow()

    private val _scanHistory = MutableStateFlow<List<LastScanResult>>(emptyList())
    val scanHistory: StateFlow<List<LastScanResult>> = _scanHistory.asStateFlow()

    private val _isShowingReview = MutableStateFlow(false)
    val isShowingReview: StateFlow<Boolean> = _isShowingReview.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    // Kamera için geçici URI
    private val _pendingCameraUri = MutableStateFlow<Uri?>(null)
    val pendingCameraUri: StateFlow<Uri?> = _pendingCameraUri.asStateFlow()

    init {
        // Mock data removed to keep initial state empty
    }

    /**
     * Fotoğrafı analiz için backend'e gönderir.
     */
    fun analyzeImage(context: Context, uri: Uri, token: String) {
        if (token.isEmpty()) {
            _error.value = "Oturum açılmamış. Lütfen tekrar giriş yapın."
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            _error.value = null
            _saveSuccess.value = false
            
            val result = repository.analyzeImage(token, uri, context)
            
            result.onSuccess { response ->
                _lastScanResult.value = LastScanResult(
                    foodName = response.foodName,
                    calories = response.calories,
                    baseCalories = (response.calories / response.portionSize).toInt(),
                    protein = response.protein,
                    carbs = response.carbs,
                    fat = response.fat,
                    portion = response.portionSize
                )
                _isAnalyzing.value = false
                _isShowingReview.value = true // Analiz bitince onay ekranını aç
            }.onFailure { exception ->
                if (exception.message?.contains("401") == true) {
                    _error.value = "Oturum süresi doldu. Lütfen yeniden giriş yapın."
                } else {
                    _error.value = exception.message ?: "Analiz sırasında bir hata oluştu"
                }
                _isAnalyzing.value = false
            }
        }
    }

    /**
     * Porsiyonu günceller ve kaloriyi/makroları yeniden hesaplar.
     */
    fun updatePortion(newPortion: Float) {
        _lastScanResult.value = _lastScanResult.value?.let { current ->
            val ratio = newPortion / current.portion
            val newCalories = (current.baseCalories * newPortion).roundToInt()
            current.copy(
                portion = newPortion, 
                calories = newCalories,
                protein = current.protein * ratio,
                carbs = current.carbs * ratio,
                fat = current.fat * ratio
            )
        }
    }

    /**
     * Yemek adını manuel günceller.
     */
    fun updateFoodName(newName: String) {
        _lastScanResult.value = _lastScanResult.value?.copy(foodName = newName)
    }

    /**
     * Onay ekranını kapatır.
     */
    fun dismissReview() {
        _isShowingReview.value = false
    }

    /**
     * Öğünü onaylar ve kaydeder.
     */
    fun confirmMeal(token: String) {
        val currentScan = _lastScanResult.value
        if (currentScan == null || token.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            
            val request = SaveNutritionRequest(
                foodName = currentScan.foodName,
                calories = currentScan.calories,
                protein = currentScan.protein,
                carbs = currentScan.carbs,
                fat = currentScan.fat,
                portion = currentScan.portion
            )
            
            val result = repository.saveMeal(token, request)
            
            result.onSuccess {
                _isSaving.value = false
                _isShowingReview.value = false
                _saveSuccess.value = true
                
                // Başarılı kaydı yerel geçmişe ekle
                currentScan.let { scan ->
                    _scanHistory.value = _scanHistory.value + scan
                }
                
                // Opsiyonel: Scan sonucunu temizle
                _lastScanResult.value = null
                _selectedImageUri.value = null
            }.onFailure { exception ->
                if (exception.message?.contains("401") == true) {
                    _error.value = "Oturum süresi doldu. Lütfen yeniden giriş yapın."
                } else {
                    _error.value = exception.message ?: "Kayıt sırasında bir hata oluştu"
                }
                _isSaving.value = false
            }
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    fun createPhotoUri(context: Context): Uri {
        val photoDir = File(context.cacheDir, "camera_photos")
        photoDir.mkdirs()
        val photoFile = File(photoDir, "photo_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        _pendingCameraUri.value = uri
        return uri
    }

    fun onPhotoTaken(success: Boolean, context: Context, token: String) {
        if (success) {
            val uri = _pendingCameraUri.value
            if (uri != null) {
                _selectedImageUri.value = uri
                analyzeImage(context, uri, token)
            }
        }
        _pendingCameraUri.value = null
    }

    fun onImageSelected(uri: Uri, context: Context, token: String) {
        _selectedImageUri.value = uri
        analyzeImage(context, uri, token)
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
        _isAnalyzing.value = false
        _error.value = null
        _isShowingReview.value = false
        _saveSuccess.value = false
    }

    fun resetAnalysis() {
        _selectedImageUri.value = null
        _isAnalyzing.value = false
        _error.value = null
    }

    /**
     * Yerel tarama geçmişini temizler.
     */
    fun clearHistory() {
        _scanHistory.value = emptyList()
    }
}

data class LastScanResult(
    val foodName: String,
    val calories: Int,
    val baseCalories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val portion: Float
)
