package com.dogukanpayal.victus_frontend.ui.profile

import androidx.lifecycle.ViewModel
import com.dogukanpayal.victus_frontend.ui.setup_profile.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileState(
    val name: String = "Ahmet Yılmaz",
    val email: String = "ahmet.yilmaz@fitnessapp.com",
    val heightCm: Int = 182,
    val weightKg: Int = 84,
    val fitnessGoal: Goal = Goal.GAIN_MUSCLE,
    val version: String = "2.4.1 (BUILD 890)"
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun onEditProfileClicked() {
        // No function as requested
    }

    fun onLogoutClicked() {
        // No function as requested
    }
}
