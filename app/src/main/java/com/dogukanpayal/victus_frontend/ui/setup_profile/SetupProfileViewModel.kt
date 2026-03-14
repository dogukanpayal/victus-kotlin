package com.dogukanpayal.victus_frontend.ui.setup_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.ProfileResponse
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepository
import com.dogukanpayal.victus_frontend.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

            val result =
                    profileRepository.updateProfile(
                            accessToken = accessToken,
                            email = email,
                            heightCm = _heightCm.value.toDouble(),
                            weightKg = _weightKg.value.toDouble(),
                            age = _age.value,
                            sex = _selectedSex.value
                    )

            result
                    .onSuccess { response ->
                        _profileResponse.value = response
                        _updateState.value = ProfileUpdateState.SUCCESS
                        _errorMessage.value = ""
                    }
                    .onFailure { exception ->
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
}
