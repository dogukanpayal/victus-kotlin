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
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val heightCm: Int = 0,
    val weightKg: Int = 0,
    val bmr: Double = 0.0,
    val dailyCalories: Double = 0.0,
    val fitnessGoal: Goal = Goal.LOSE_WEIGHT,
    val version: String = "2.4.1 (BUILD 890)",
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun loadUserProfile(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                Log.d(TAG, "Fetching user profile from backend...")
                
                val result = profileRepository.getProfile(accessToken)
                
                result.onSuccess { profileResponse ->
                    Log.d(TAG, "Profile loaded successfully:")
                    Log.d(TAG, "Full Name: ${profileResponse.fullName ?: profileResponse.email}")
                    Log.d(TAG, "Email: ${profileResponse.email}")
                    Log.d(TAG, "Height: ${profileResponse.heightCm} cm")
                    Log.d(TAG, "Weight: ${profileResponse.weightKg} kg")
                    Log.d(TAG, "BMR: ${profileResponse.bmr} kcal")
                    Log.d(TAG, "Daily Calories: ${profileResponse.dailyCalories} kcal")
                    Log.d(TAG, "Goal: ${profileResponse.goal}")
                    
                    val goalEnum = when (profileResponse.goal) {
                        "LOSE_WEIGHT" -> Goal.LOSE_WEIGHT
                        "GAIN_MUSCLE" -> Goal.GAIN_MUSCLE
                        "STAY_FIT" -> Goal.STAY_IN_SHAPE
                        else -> Goal.LOSE_WEIGHT
                    }
                    
                    // Fallback: fullName boşsa email, onun da boşsa "Kullanıcı" kullan
                    val displayName = if (!profileResponse.fullName.isNullOrBlank()) {
                        profileResponse.fullName
                    } else if (profileResponse.email.isNotBlank()) {
                        profileResponse.email
                    } else {
                        "Kullanıcı"
                    }
                    
                    _uiState.value = ProfileState(
                        name = displayName,
                        email = profileResponse.email,
                        heightCm = profileResponse.heightCm.toInt(),
                        weightKg = profileResponse.weightKg.toInt(),
                        bmr = profileResponse.bmr,
                        dailyCalories = profileResponse.dailyCalories ?: 0.0,
                        fitnessGoal = goalEnum,
                        version = "2.4.1 (BUILD 890)",
                        isLoading = false
                    )
                    
                    Log.d(TAG, "State Updated Successfully!")
                    Log.d(TAG, "Updated State - Name: ${_uiState.value.name}")
                    Log.d(TAG, "Updated State - Height: ${_uiState.value.heightCm} cm")
                    Log.d(TAG, "Updated State - Weight: ${_uiState.value.weightKg} kg")
                    Log.d(TAG, "Updated State - BMR: ${_uiState.value.bmr} kcal")
                    Log.d(TAG, "Updated State - Daily Calories: ${_uiState.value.dailyCalories} kcal")
                }
                
                result.onFailure { exception ->
                    Log.e(TAG, "Failed to load profile: ${exception.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onEditProfileClicked() {
        // No function as requested
    }

    fun onLogoutClicked() {
        // No function as requested
    }
}


