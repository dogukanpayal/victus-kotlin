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

    // Comparison States
    private val _isComparisonOpen = MutableStateFlow(false)
    val isComparisonOpen: StateFlow<Boolean> = _isComparisonOpen.asStateFlow()

    private val _selectedBefore = MutableStateFlow<HealthMetricsData?>(null)
    val selectedBefore: StateFlow<HealthMetricsData?> = _selectedBefore.asStateFlow()

    private val _selectedAfter = MutableStateFlow<HealthMetricsData?>(null)
    val selectedAfter: StateFlow<HealthMetricsData?> = _selectedAfter.asStateFlow()

    private val _comparisonResult = MutableStateFlow<com.dogukanpayal.victus_frontend.data.model.ComparisonResult?>(null)
    val comparisonResult: StateFlow<com.dogukanpayal.victus_frontend.data.model.ComparisonResult?> = _comparisonResult.asStateFlow()

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

            val profileResult = profileRepo.getProfile(token)
            val currentWeight = profileResult.getOrNull()?.weightKg ?: 0.0

            if (currentWeight <= 0) {
                _error.value = "Profilinizde kilo bilgisi bulunamadı. Lütfen profilinizi güncelleyin."
                _isAnalyzing.value = false
                return@launch
            }

            bodyRepo.analyzeBody(token, uri, context, currentWeight)
                .onSuccess { report ->
                    _analysisResult.value = report
                    _isAnalyzing.value = false
                    loadMetricsHistory(token)
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Analiz sırasında bir hata oluştu"
                    _isAnalyzing.value = false
                }
        }
    }

    private val _isComparisonLoading = MutableStateFlow(false)
    val isComparisonLoading: StateFlow<Boolean> = _isComparisonLoading.asStateFlow()

    fun openComparison() {
        _isComparisonOpen.value = true
        _comparisonResult.value = null
        _selectedBefore.value = null
        _selectedAfter.value = null
    }

    fun closeComparison() {
        _isComparisonOpen.value = false
    }

    fun selectBefore(metric: HealthMetricsData) {
        _selectedBefore.value = metric
    }

    fun selectAfter(metric: HealthMetricsData) {
        _selectedAfter.value = metric
    }

    fun performComparison(token: String) {
        val before = _selectedBefore.value
        val after = _selectedAfter.value
        
        if (before != null && after != null && token.isNotEmpty()) {
            val fatDelta = after.fatPercentage - before.fatPercentage
            val muscleDelta = after.musclePercentage - before.musclePercentage
            val weightDelta = after.weight - before.weight
            val bmiDelta = after.bmi - before.bmi
            
            // Calculate days difference
            val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val beforeDate = try { format.parse(before.createdAt.take(10)) } catch (e: Exception) { null }
            val afterDate = try { format.parse(after.createdAt.take(10)) } catch (e: Exception) { null }
            
            val daysDiff = if (beforeDate != null && afterDate != null) {
                val diff = afterDate.time - beforeDate.time
                (diff / (1000 * 60 * 60 * 24)).toInt()
            } else 0

            _isComparisonLoading.value = true
            
            viewModelScope.launch {
                val request = com.dogukanpayal.victus_frontend.data.model.ProgressAnalysisRequest(
                    beforeMetrics = com.dogukanpayal.victus_frontend.data.model.ProgressMetrics(
                        weight = before.weight,
                        fatPercentage = before.fatPercentage,
                        musclePercentage = before.musclePercentage,
                        bmi = before.bmi,
                        postureNotes = before.postureNotes
                    ),
                    afterMetrics = com.dogukanpayal.victus_frontend.data.model.ProgressMetrics(
                        weight = after.weight,
                        fatPercentage = after.fatPercentage,
                        musclePercentage = after.musclePercentage,
                        bmi = after.bmi,
                        postureNotes = after.postureNotes
                    ),
                    daysDifference = daysDiff
                )

                val summary = bodyRepo.analyzeProgress(token, request).getOrNull()?.summary 
                    ?: "$daysDiff gün içerisinde yaklaşık ${String.format("%.1f", Math.abs(weightDelta))} kg ${if (weightDelta < 0) "verdiniz" else "aldınız"}."

                _comparisonResult.value = com.dogukanpayal.victus_frontend.data.model.ComparisonResult(
                    before = before,
                    after = after,
                    fatDelta = fatDelta,
                    muscleDelta = muscleDelta,
                    weightDelta = weightDelta,
                    bmiDelta = bmiDelta,
                    daysDifference = daysDiff,
                    summaryText = summary
                )
                
                _isComparisonLoading.value = false
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

    fun deleteMetrics(token: String, id: String) {
        if (token.isEmpty()) return
        viewModelScope.launch {
            bodyRepo.deleteMetrics(token, id).onSuccess {
                loadMetricsHistory(token)
            }
        }
    }
}
