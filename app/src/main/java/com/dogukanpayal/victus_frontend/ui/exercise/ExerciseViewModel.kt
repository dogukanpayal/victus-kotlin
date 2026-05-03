package com.dogukanpayal.victus_frontend.ui.exercise

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.BodyCompositionReport
import com.dogukanpayal.victus_frontend.data.model.HealthMetricsData
import com.dogukanpayal.victus_frontend.data.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ExerciseViewModel(
    private val bodyRepo: BodyAnalysisRepository = BodyAnalysisRepositoryImpl(),
    private val profileRepo: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<BodyCompositionReport?>(null)
    val analysisResult: StateFlow<BodyCompositionReport?> = _analysisResult.asStateFlow()

    private val _metricsHistory = MutableStateFlow<List<HealthMetricsData>>(emptyList())
    val metricsHistory: StateFlow<List<HealthMetricsData>> = _metricsHistory.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pendingCameraUri = MutableStateFlow<Uri?>(null)
    val pendingCameraUri: StateFlow<Uri?> = _pendingCameraUri.asStateFlow()

    fun loadMetricsHistory(token: String) {
        if (token.isEmpty()) return
        viewModelScope.launch {
            bodyRepo.getMetricsHistory(token).onSuccess {
                _metricsHistory.value = it
            }
        }
    }

    fun analyzeBodyPhoto(context: Context, uri: Uri, token: String) {
        if (token.isEmpty()) {
            _error.value = "Lütfen giriş yapın."
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            _error.value = null

            // 1. Get current weight from profile
            val profileResult = profileRepo.getProfile(token)
            val currentWeight = profileResult.getOrNull()?.weightKg ?: 0.0

            if (currentWeight <= 0) {
                _error.value = "Profilinizde kilo bilgisi bulunamadı. Lütfen profilinizi güncelleyin."
                _isAnalyzing.value = false
                return@launch
            }

            // 2. Perform AI analysis
            bodyRepo.analyzeBody(token, uri, context, currentWeight)
                .onSuccess { report ->
                    _analysisResult.value = report
                    _isAnalyzing.value = false
                    loadMetricsHistory(token) // Refresh history
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Analiz sırasında bir hata oluştu"
                    _isAnalyzing.value = false
                }
        }
    }

    fun createPhotoUri(context: Context): Uri {
        val photoDir = File(context.cacheDir, "body_photos")
        photoDir.mkdirs()
        val photoFile = File(photoDir, "body_${System.currentTimeMillis()}.jpg")
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
                analyzeBodyPhoto(context, uri, token)
            }
        }
        _pendingCameraUri.value = null
    }

    fun onImageSelected(uri: Uri, context: Context, token: String) {
        _selectedImageUri.value = uri
        analyzeBodyPhoto(context, uri, token)
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
        _analysisResult.value = null
        _error.value = null
    }

    fun dismissResult() {
        _analysisResult.value = null
    }
}
