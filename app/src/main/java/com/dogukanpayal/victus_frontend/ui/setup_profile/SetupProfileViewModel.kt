package com.dogukanpayal.victus_frontend.ui.setup_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.ProfileResponse
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepository
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "SetupProfileViewModel"

enum class Goal {
    LOSE_WEIGHT,
    GAIN_MUSCLE,
    STAY_IN_SHAPE
}

enum class ProfileUpdateState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

class SetupProfileViewModel(
        private val profileRepository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {

    // Golden Case default values: 180cm, 75kg, 25 yaş, Erkek
    private val _heightCm = MutableStateFlow(180f)
    val heightCm: StateFlow<Float> = _heightCm.asStateFlow()

    private val _weightKg = MutableStateFlow(75f)
    val weightKg: StateFlow<Float> = _weightKg.asStateFlow()

    private val _selectedGoal = MutableStateFlow(Goal.LOSE_WEIGHT)
    val selectedGoal: StateFlow<Goal> = _selectedGoal.asStateFlow()

    private val _age = MutableStateFlow(25)
    val age: StateFlow<Int> = _age.asStateFlow()

    private val _selectedSex = MutableStateFlow("male")
    val selectedSex: StateFlow<String> = _selectedSex.asStateFlow()

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.IDLE)
    val updateState: StateFlow<ProfileUpdateState> = _updateState.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _profileResponse = MutableStateFlow<ProfileResponse?>(null)
    val profileResponse: StateFlow<ProfileResponse?> = _profileResponse.asStateFlow()

    fun onHeightChanged(height: Float) {
        _heightCm.value = height
    }

    fun onWeightChanged(weight: Float) {
        _weightKg.value = weight
    }

    fun onGoalSelected(goal: Goal) {
        _selectedGoal.value = goal
        Log.d(TAG, "Goal selected: ${goalToString(goal)}")
    }

    fun onAgeChanged(newAge: Int) {
        _age.value = newAge
    }

    fun onSexSelected(sex: String) {
        _selectedSex.value = sex
    }

    fun onContinueClicked(accessToken: String, email: String) {
        viewModelScope.launch {
            _updateState.value = ProfileUpdateState.LOADING

            val heightValue = _heightCm.value.roundToInt().toDouble()
            val weightValue = _weightKg.value.roundToInt().toDouble()
            val ageValue = _age.value
            val sexValue = _selectedSex.value
            val goalValue = goalToString(_selectedGoal.value)

            // Golden Case Logging: 180cm, 75kg, 25 yaş, Erkek, LOSE_WEIGHT
            Log.d(TAG, "Profile Update Request:")
            Log.d(TAG, "Height: $heightValue cm")
            Log.d(TAG, "Weight: $weightValue kg")
            Log.d(TAG, "Age: $ageValue")
            Log.d(TAG, "Sex: $sexValue")
            Log.d(TAG, "Goal: $goalValue")
            Log.d(TAG, "Expected BMR formula: (10*$weightValue + 6.25*$heightValue - 5*$ageValue + 5 for male)")
            Log.d(TAG, "Expected BMR ≈ 1755 kcal (for 180cm, 75kg, 25 yaş, Erkek)")
            Log.d(TAG, "Expected Daily Calories (LOSE_WEIGHT): BMR×1.2−500 = 1755×1.2−500 = 1606 kcal")

            val result =
                    profileRepository.updateProfile(
                            accessToken = accessToken,
                            email = email,
                            heightCm = heightValue,
                            weightKg = weightValue,
                            age = ageValue,
                            sex = sexValue,
                            goal = goalValue
                    )

            result
                    .onSuccess { response ->
                        Log.d(TAG, "Profile Update Success:")
                        Log.d(TAG, "BMR: ${response.bmr} kcal")
                        Log.d(TAG, "Daily Calories: ${response.daily_calories} kcal")
                        Log.d(TAG, "Goal: ${response.goal}")
                        _profileResponse.value = response
                        _updateState.value = ProfileUpdateState.SUCCESS
                        _errorMessage.value = ""
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Profile Update Failed: ${exception.message}", exception)
                        _updateState.value = ProfileUpdateState.ERROR
                        _errorMessage.value =
                                exception.message ?: "Profil güncellenirken hata oluştu"
                    }
        }
    }

    fun clearError() {
        _errorMessage.value = ""
        _updateState.value = ProfileUpdateState.IDLE
    }

    private fun goalToString(goal: Goal): String {
        return when (goal) {
            Goal.LOSE_WEIGHT -> "LOSE_WEIGHT"
            Goal.GAIN_MUSCLE -> "GAIN_MUSCLE"
            Goal.STAY_IN_SHAPE -> "STAY_FIT"
        }
    }
}
