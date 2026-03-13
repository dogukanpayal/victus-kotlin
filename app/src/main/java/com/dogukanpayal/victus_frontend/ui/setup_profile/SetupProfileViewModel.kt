package com.dogukanpayal.victus_frontend.ui.setup_profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Goal {
    LOSE_WEIGHT,
    GAIN_MUSCLE,
    STAY_IN_SHAPE
}

class SetupProfileViewModel : ViewModel() {

    private val _heightCm = MutableStateFlow(175f)
    val heightCm: StateFlow<Float> = _heightCm.asStateFlow()

    private val _weightKg = MutableStateFlow(70f)
    val weightKg: StateFlow<Float> = _weightKg.asStateFlow()

    private val _selectedGoal = MutableStateFlow(Goal.LOSE_WEIGHT)
    val selectedGoal: StateFlow<Goal> = _selectedGoal.asStateFlow()

    fun onHeightChanged(height: Float) {
        _heightCm.value = height
    }

    fun onWeightChanged(weight: Float) {
        _weightKg.value = weight
    }

    fun onGoalSelected(goal: Goal) {
        _selectedGoal.value = goal
    }

    fun onContinueClicked() {
        // Dummy action for now. Will connect to backend later.
        println("Continue clicked with Height: ${_heightCm.value}, Weight: ${_weightKg.value}, Goal: ${_selectedGoal.value}")
    }
}
