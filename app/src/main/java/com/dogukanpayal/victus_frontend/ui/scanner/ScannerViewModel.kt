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

    private val _lastScanResults = MutableStateFlow<List<LastScanResult>>(emptyList())
    val lastScanResults: StateFlow<List<LastScanResult>> = _lastScanResults.asStateFlow()

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
                val scanResults = response.results.map { item ->
                    LastScanResult(
                        foodName = item.foodName,
                        calories = item.calories,
                        baseCalories = (item.calories / (if (item.portionSize > 0) item.portionSize else 1f)).toInt(),
                        protein = item.protein,
                        carbs = item.carbs,
                        fat = item.fat,
                        portion = item.portionSize
                    )
                }
                _lastScanResults.value = scanResults
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
     * Manuel yemek girişi analiz eder (Metin tabanlı).
     */
    fun analyzeTextPortion(query: String, portion: Double, token: String) {
        if (token.isEmpty()) {
            _error.value = "Oturum açılmamış. Lütfen tekrar giriş yapın."
            return
        }

        if (query.isEmpty()) {
            _error.value = "Yemek adı boş olamaz."
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            _error.value = null
            _saveSuccess.value = false
            
            val result = repository.analyzeText(token, query, portion)
            
            result.onSuccess { response ->
                val scanResults = response.results.map { item ->
                    LastScanResult(
                        foodName = item.foodName,
                        calories = item.calories,
                        baseCalories = (item.calories / (if (item.portionSize > 0) item.portionSize else 1f)).toInt(),
                        protein = item.protein,
                        carbs = item.carbs,
                        fat = item.fat,
                        portion = item.portionSize
                    )
                }
                // Manuel girişi mevcut listeye EKLE
                _lastScanResults.value = _lastScanResults.value + scanResults
                _isAnalyzing.value = false
                _isShowingReview.value = true
            }.onFailure { exception ->
                _error.value = exception.message ?: "Metin analizi sırasında bir hata oluştu"
                _isAnalyzing.value = false
            }
        }
    }

    /**
     * Porsiyonu günceller (Tüm liste için veya belirli bir index için eklenebilir, şimdilik basit tutuyoruz)
     */
    fun updatePortion(index: Int, newPortion: Float) {
        val currentList = _lastScanResults.value.toMutableList()
        if (index in currentList.indices) {
            val current = currentList[index]
            val ratio = newPortion / (if (current.portion > 0) current.portion else 1f)
            val newCalories = (current.baseCalories * newPortion).roundToInt()
            currentList[index] = current.copy(
                portion = newPortion,
                calories = newCalories,
                protein = current.protein * ratio,
                carbs = current.carbs * ratio,
                fat = current.fat * ratio
            )
            _lastScanResults.value = currentList
        }
    }

    /**
     * Yemek adını manuel günceller.
     */
    fun updateFoodName(index: Int, newName: String) {
        val currentList = _lastScanResults.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(foodName = newName)
            _lastScanResults.value = currentList
        }
    }

    /**
     * Bir tarama sonucunu listeden çıkarır.
     */
    fun removeScanResult(index: Int) {
        val currentList = _lastScanResults.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _lastScanResults.value = currentList
            
            // Eğer liste boşalırsa onay sayfasını kapat
            if (currentList.isEmpty()) {
                _isShowingReview.value = false
            }
        }
    }

    /**
     * Onay ekranını kapatır.
     */
    fun dismissReview() {
        _isShowingReview.value = false
    }

    /**
     * Tüm saptanan öğünleri onaylar ve kaydeder.
     */
    fun confirmAllMeals(token: String) {
        val currentScans = _lastScanResults.value
        if (currentScans.isEmpty() || token.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            
            var success = true
            currentScans.forEach { scan ->
                val request = SaveNutritionRequest(
                    foodName = scan.foodName,
                    calories = scan.calories,
                    protein = scan.protein,
                    carbs = scan.carbs,
                    fat = scan.fat,
                    portion = scan.portion
                )
                repository.saveMeal(token, request).onFailure { success = false }
            }
            
            if (success) {
                _isSaving.value = false
                _isShowingReview.value = false
                _saveSuccess.value = true
                
                // Başarılı kaydı yerel geçmişe ekle
                _scanHistory.value = _scanHistory.value + currentScans
                
                // Kayıt sonrası temizle
                _lastScanResults.value = emptyList()
                _selectedImageUri.value = null
            } else {
                _error.value = "Bazı öğünler kaydedilemedi."
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
