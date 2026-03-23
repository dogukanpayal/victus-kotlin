package com.dogukanpayal.victus_frontend.ui.scanner

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScannerViewModel : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _lastScanResult = MutableStateFlow<LastScanResult?>(null)
    val lastScanResult: StateFlow<LastScanResult?> = _lastScanResult.asStateFlow()

    init {
        // Mock son tarama sonucu
        _lastScanResult.value = LastScanResult(
            foodName = "Mercimek Çorbası",
            calories = 240,
            portion = 1.5f
        )
    }

    /**
     * Galeri veya kameradan fotoğraf seçildiğinde çağrılır.
     * İleride bu URI backend'e gönderilecek: POST /api/nutrition/analyze
     */
    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        // TODO: analyzeImage(uri) - Backend'e fotoğraf gönderme
    }

    /**
     * Kamera butonuna basıldığında çağrılır.
     * İleride CameraX veya Intent ile fotoğraf çekme akışını başlatır.
     */
    fun onCapturePhoto() {
        // TODO: Kamera izni kontrolü + fotoğraf çekme akışı
    }

    /**
     * Galeri butonuna basıldığında çağrılır.
     * İleride galeri seçici açılır.
     */
    fun onGalleryClick() {
        // TODO: Galeri izni kontrolü + fotoğraf seçme akışı
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
