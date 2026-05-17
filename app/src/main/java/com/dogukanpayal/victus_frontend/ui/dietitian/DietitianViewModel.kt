package com.dogukanpayal.victus_frontend.ui.dietitian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.PatientDetail
import com.dogukanpayal.victus_frontend.data.model.PatientSummary
import com.dogukanpayal.victus_frontend.data.model.SendFeedbackRequest
import com.dogukanpayal.victus_frontend.data.repository.DietitianRepository
import com.dogukanpayal.victus_frontend.data.repository.FeedbackRepository
import com.dogukanpayal.victus_frontend.data.repository.FeedbackRepositoryImpl
import com.dogukanpayal.victus_frontend.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import android.content.ContentValues
import android.os.Build
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DietitianUiState(
    val patients: List<PatientSummary> = emptyList(),
    val filteredPatients: List<PatientSummary> = emptyList(),
    val selectedPatient: PatientDetail? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null,
    val isDownloadingReport: Boolean = false,
    val reportDownloadSuccessMessage: String? = null,
    val reportDownloadError: String? = null
)

class DietitianViewModel(
    private val repository: DietitianRepository,
    private val feedbackRepository: FeedbackRepository = FeedbackRepositoryImpl(RetrofitClient.apiService)
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietitianUiState())
    val uiState: StateFlow<DietitianUiState> = _uiState.asStateFlow()

    fun loadPatients(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getPatients(token)
            result.onSuccess { patients ->
                _uiState.update { 
                    it.copy(
                        patients = patients, 
                        filteredPatients = patients,
                        isLoading = false 
                    ) 
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.patients
            } else {
                state.patients.filter { 
                    it.fullName.contains(query, ignoreCase = true) || 
                    it.email.contains(query, ignoreCase = true) 
                }
            }
            state.copy(searchQuery = query, filteredPatients = filtered)
        }
    }

    fun loadPatientDetail(token: String, patientId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val current = state.selectedPatient
                val shouldClear = current == null || current.profile.id != patientId
                state.copy(
                    isLoading = true,
                    error = null,
                    selectedPatient = if (shouldClear) null else current
                )
            }
            val result = repository.getPatientDetail(token, patientId)
            result.onSuccess { detail ->
                _uiState.update { it.copy(selectedPatient = detail, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun sendFeedback(token: String, patientId: String, title: String, message: String) {
        viewModelScope.launch {
            val request = SendFeedbackRequest(patientId, title, message)
            feedbackRepository.sendFeedback(token, request).onSuccess {
                // Success, could update UI with a toast or state
            }.onFailure {
                // Handle error
            }
        }
    }

    fun downloadWeeklyReport(token: String, patientId: String, patientName: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloadingReport = true, reportDownloadSuccessMessage = null, reportDownloadError = null) }
            val result = repository.downloadWeeklyReport(token, patientId)
            result.onSuccess { responseBody ->
                val success = withContext(Dispatchers.IO) {
                    savePdfToDownloads(responseBody.byteStream(), patientName, context)
                }
                if (success) {
                    _uiState.update { it.copy(
                        isDownloadingReport = false,
                        reportDownloadSuccessMessage = "Rapor başarıyla İndirilenler (Downloads) klasörüne kaydedildi."
                    )}
                } else {
                    _uiState.update { it.copy(
                        isDownloadingReport = false,
                        reportDownloadError = "Dosya kaydedilirken bir hata oluştu."
                    )}
                }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    isDownloadingReport = false,
                    reportDownloadError = "Rapor indirilemedi: ${e.message}"
                )}
            }
        }
    }

    private fun savePdfToDownloads(
        inputStream: InputStream,
        patientName: String,
        context: Context
    ): Boolean {
        val fileName = "Victus_Haftalik_Rapor_${patientName.replace(" ", "_")}.pdf"
        
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    true
                } else {
                    false
                }
            } else {
                val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }
                val file = File(targetDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try {
                inputStream.close()
            } catch (ignored: Exception) {}
        }
    }

    fun clearReportDownloadStatus() {
        _uiState.update { it.copy(reportDownloadSuccessMessage = null, reportDownloadError = null) }
    }

    fun clearState() {
        _uiState.value = DietitianUiState()
    }

    fun uploadPatientDietPlan(
        context: Context,
        token: String,
        patientId: String,
        uri: Uri,
        mimeType: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.uploadPatientDietPlan(context, token, patientId, uri, mimeType)
            result.onSuccess {
                // Reload patient details to update the active diet plan summary instantly!
                loadPatientDetail(token, patientId)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Diyet yüklenemedi: ${e.message}") }
            }
        }
    }
}
