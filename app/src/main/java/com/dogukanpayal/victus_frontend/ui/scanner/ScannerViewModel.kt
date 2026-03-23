package com.dogukanpayal.victus_frontend.ui.scanner

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ScannerViewModel : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _lastScanResult = MutableStateFlow<LastScanResult?>(null)
    val lastScanResult: StateFlow<LastScanResult?> = _lastScanResult.asStateFlow()

    // Kamera için geçici URI (FileProvider tarafından oluşturulur)
    private val _pendingCameraUri = MutableStateFlow<Uri?>(null)
    val pendingCameraUri: StateFlow<Uri?> = _pendingCameraUri.asStateFlow()

    init {
        // Mock son tarama sonucu
        _lastScanResult.value = LastScanResult(
            foodName = "Mercimek Çorbası",
            calories = 240,
            portion = 1.5f
        )
    }

    /**
     * Kamera ile fotoğraf çekmeden önce geçici bir URI oluşturur.
     * FileProvider kullanarak cache dizininde güvenli bir dosya oluşturur.
     */
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

    /**
     * Kamera fotoğraf çektikten sonra çağrılır.
     * success = true ise fotoğraf başarıyla çekildi, URI set edilir.
     */
    fun onPhotoTaken(success: Boolean) {
        if (success) {
            _selectedImageUri.value = _pendingCameraUri.value
            // TODO: İleride analyzeImage() çağrılacak — POST /api/nutrition/analyze
        }
        _pendingCameraUri.value = null
    }

    /**
     * Galeriden fotoğraf seçildiğinde çağrılır.
     */
    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        // TODO: İleride analyzeImage() çağrılacak — POST /api/nutrition/analyze
    }

    /**
     * Seçilen fotoğrafı temizler (tekrar çekim için)
     */
    fun clearSelectedImage() {
        _selectedImageUri.value = null
        _isAnalyzing.value = false
    }

    /**
     * Analizi resetle
     */
    fun resetAnalysis() {
        _selectedImageUri.value = null
        _isAnalyzing.value = false
    }
}

data class LastScanResult(
    val foodName: String,
    val calories: Int,
    val portion: Float
)
