package com.dogukanpayal.victus_frontend.ui.profile

import androidx.lifecycle.ViewModel
import com.dogukanpayal.victus_frontend.ui.setup_profile.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditProfileState(
    val name: String = "Arda Yılmaz",
    val email: String = "arda.yilmaz@fitnessapp.com",
    val heightCm: String = "185",
    val weightKg: String = "82",
    val fitnessGoal: Goal = Goal.GAIN_MUSCLE,
    val workoutRemindersEnabled: Boolean = true
)

class EditProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

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

    fun onWorkoutRemindersToggled(enabled: Boolean) {
        _uiState.update { it.copy(workoutRemindersEnabled = enabled) }
    }

    fun onSaveClicked() {
        // No function as requested
    }
    
    fun onChangePasswordClicked() {
        // No function as requested
    }

    fun onChangePhotoClicked() {
        // No function as requested
    }
}
