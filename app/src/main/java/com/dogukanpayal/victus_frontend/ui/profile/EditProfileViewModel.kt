package com.dogukanpayal.victus_frontend.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepository
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepositoryImpl
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
    val isLoading: Boolean = true,
    val updateState: EditProfileUpdateState = EditProfileUpdateState.IDLE,
    val errorMessage: String = ""
)

class EditProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    fun loadUserProfile(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                Log.d(TAG, "Fetching user profile for editing...")
                
                val result = profileRepository.getProfile(accessToken)
                
                result.onSuccess { profileResponse ->
                    Log.d(TAG, "Profile loaded successfully for editing")
                    
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
                            isLoading = false,
                            updateState = EditProfileUpdateState.IDLE
                        )
                    }
                    
                    Log.d(TAG, "Profile state updated successfully")
                }
                
                result.onFailure { exception ->
                    Log.e(TAG, "Failed to load profile: ${exception.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            updateState = EditProfileUpdateState.ERROR,
                            errorMessage = exception.message ?: "Profil yükleme başarısız"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile: ${e.message}")
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
                
                Log.d(TAG, "Saving profile changes...")
                
                val heightValue = currentState.heightCm.toDoubleOrNull() ?: 0.0
                val weightValue = currentState.weightKg.toDoubleOrNull() ?: 0.0
                val goalString = goalToString(currentState.fitnessGoal)
                
                Log.d(TAG, "Update Request: name=${currentState.name}, height=$heightValue, weight=$weightValue, goal=$goalString")
                
                val result = profileRepository.updateProfile(
                    accessToken = accessToken,
                    email = currentState.email,
                    fullName = currentState.name,
                    heightCm = heightValue,
                    weightKg = weightValue,
                    age = currentState.age,
                    sex = currentState.sex,
                    goal = goalString
                )
                
                result.onSuccess { _ ->
                    Log.d(TAG, "Profile updated successfully")
                    _uiState.update {
                        it.copy(
                            updateState = EditProfileUpdateState.SUCCESS,
                            errorMessage = ""
                        )
                    }
                    onSuccess()
                }
                
                result.onFailure { exception ->
                    Log.e(TAG, "Profile update failed: ${exception.message}")
                    _uiState.update {
                        it.copy(
                            updateState = EditProfileUpdateState.ERROR,
                            errorMessage = exception.message ?: "Profil güncellenirken hata oluştu"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile: ${e.message}")
                _uiState.update {
                    it.copy(
                        updateState = EditProfileUpdateState.ERROR,
                        errorMessage = e.message ?: "Profil güncellenirken hata oluştu"
                    )
                }
            }
        }
    }

    fun onChangePhotoClicked() {
        // No function as requested
    }

    private fun goalToString(goal: Goal): String {
        return when (goal) {
            Goal.LOSE_WEIGHT -> "LOSE_WEIGHT"
            Goal.GAIN_MUSCLE -> "GAIN_MUSCLE"
            Goal.STAY_IN_SHAPE -> "STAY_FIT"
        }
    }
}
