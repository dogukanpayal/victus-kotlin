package com.dogukanpayal.victus_frontend.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.Exercise
import com.dogukanpayal.victus_frontend.data.model.WorkoutLogRequest
import com.dogukanpayal.victus_frontend.data.model.WorkoutPreset
import com.dogukanpayal.victus_frontend.data.remote.RetrofitClient
import com.dogukanpayal.victus_frontend.data.repository.WorkoutPresetRepository
import com.dogukanpayal.victus_frontend.data.repository.DailyWorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutUiState(
    val isLoading: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
    val presets: List<WorkoutPreset> = emptyList(),
    val activePresetIds: List<String> = emptyList(),
    val completedExerciseIds: Set<String> = emptySet(),
    val completedLogs: Map<String, String> = emptyMap(),
    val burnedCaloriesForToday: Double = 0.0,
    val error: String? = null,
    val successMessage: String? = null
)

class WorkoutViewModel(
    private val presetRepository: WorkoutPresetRepository,
    private val dailyWorkoutRepository: DailyWorkoutRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        loadPresets()
        loadDailyState()
    }

    private fun loadPresets() {
        _uiState.update { it.copy(presets = presetRepository.getPresets()) }
    }

    private fun loadDailyState() {
        _uiState.update { 
            it.copy(
                activePresetIds = dailyWorkoutRepository.getActivePresetIds(),
                completedExerciseIds = dailyWorkoutRepository.getCompletedExerciseIds(),
                completedLogs = dailyWorkoutRepository.getCompletedLogs()
            )
        }
    }

    fun addWorkoutForToday(presetId: String) {
        dailyWorkoutRepository.addActivePresetId(presetId)
        loadDailyState()
    }

    fun removeWorkoutForToday(token: String, preset: WorkoutPreset) {
        viewModelScope.launch {
            // Bu antrenman şablonundaki tamamlanmış hareketleri bul ve sil
            val completedInThisPreset = preset.exercises.filter { it.id in _uiState.value.completedExerciseIds }
            
            for (exercise in completedInThisPreset) {
                val logId = _uiState.value.completedLogs[exercise.id]
                if (logId != null) {
                    try {
                        RetrofitClient.apiService.deleteWorkoutLog("Bearer $token", logId)
                    } catch (e: Exception) {
                        // Hata olsa da devam et (yerel olarak temizleyeceğiz)
                    }
                }
                dailyWorkoutRepository.removeCompletedExercise(exercise.id)
            }
            
            dailyWorkoutRepository.removeActivePresetId(preset.id)
            loadDailyState()
            loadBurnedCalories(token)
        }
    }

    fun loadBurnedCalories(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getDailySummary("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val burned = response.body()?.caloriesBurned ?: 0.0
                    _uiState.update { it.copy(burnedCaloriesForToday = burned) }
                }
            } catch (e: Exception) {
                // Ignore silent failures for fetching summary
            }
        }
    }

    fun loadExercises(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.apiService.getExercises("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(exercises = response.body()!!, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Egzersizler yüklenemedi: ${response.code()}", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Bağlantı hatası", isLoading = false) }
            }
        }
    }

    fun savePreset(preset: WorkoutPreset) {
        presetRepository.savePreset(preset)
        loadPresets()
    }

    fun deletePreset(token: String, presetId: String) {
        val preset = _uiState.value.presets.find { it.id == presetId }
        presetRepository.deletePreset(presetId)
        loadPresets()
        if (preset != null && _uiState.value.activePresetIds.contains(presetId)) {
            removeWorkoutForToday(token, preset)
        }
    }

    fun logWorkout(token: String, presetExercise: com.dogukanpayal.victus_frontend.data.model.WorkoutPresetExercise) {
        if (_uiState.value.completedExerciseIds.contains(presetExercise.id)) {
            // Already completed, allow unchecking
            unlogWorkout(token, presetExercise.id)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                // Calculate duration based on type
                val durationMins = if (presetExercise.isDurationBased) {
                    presetExercise.durationMinutes?.coerceAtLeast(1) ?: 1
                } else {
                    // Heuristic: 1 set of 1 exercise roughly takes 1.5 minutes including rest
                    ((presetExercise.sets ?: 1) * 1.5).toInt().coerceAtLeast(1)
                }

                val request = WorkoutLogRequest(exerciseId = presetExercise.exerciseId, durationMinutes = durationMins)
                val response = RetrofitClient.apiService.logWorkout("Bearer $token", request)
                
                if (response.isSuccessful && response.body() != null) {
                    val logResponse = response.body()!!
                    dailyWorkoutRepository.addCompletedExercise(presetExercise.id, logResponse.id)
                    loadDailyState() // UI'ı güncelle
                    
                    // Add the burned calories from the response directly instead of re-fetching to save time
                    val caloriesBurned = logResponse.caloriesBurned
                    _uiState.update { 
                        it.copy(
                            successMessage = "${presetExercise.exerciseName} başarıyla kaydedildi!", 
                            isLoading = false,
                            burnedCaloriesForToday = it.burnedCaloriesForToday + caloriesBurned
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(error = "Antrenman kaydedilemedi: ${response.code()}", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Bağlantı hatası", isLoading = false) }
            }
        }
    }

    fun unlogWorkout(token: String, presetExerciseId: String) {
        val logId = _uiState.value.completedLogs[presetExerciseId]
        
        if (logId == null) {
            // Eğer log ID'si yoksa (eski kayıt veya bir hata nedeniyle), sadece yerel olarak kaldır
            dailyWorkoutRepository.removeCompletedExercise(presetExerciseId)
            loadDailyState()
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val response = RetrofitClient.apiService.deleteWorkoutLog("Bearer $token", logId)
                if (response.isSuccessful) {
                    dailyWorkoutRepository.removeCompletedExercise(presetExerciseId)
                    loadDailyState()
                    // Kalorileri tekrar yükle
                    loadBurnedCalories(token)
                    _uiState.update { it.copy(isLoading = false, successMessage = "Antrenman kaydı kaldırıldı.") }
                } else {
                    // Silme başarısız olsa bile yerel olarak kaldırmayı deneyebiliriz ya da hata gösteririz
                    _uiState.update { it.copy(error = "Kayıt sunucudan silinemedi: ${response.code()}", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Bağlantı hatası", isLoading = false) }
            }
        }
    }

    fun resetCalories(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val response = RetrofitClient.apiService.resetWorkoutLogs("Bearer $token")
                if (response.isSuccessful) {
                    dailyWorkoutRepository.clearCompletedExercises()
                    loadDailyState()
                    _uiState.update { it.copy(isLoading = false, burnedCaloriesForToday = 0.0, successMessage = "Tüm antrenman kayıtları sıfırlandı.") }
                } else {
                    _uiState.update { it.copy(error = "Sıfırlama başarısız: ${response.code()}", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Bağlantı hatası", isLoading = false) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
