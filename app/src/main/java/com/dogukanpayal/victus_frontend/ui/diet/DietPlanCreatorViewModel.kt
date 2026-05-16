package com.dogukanpayal.victus_frontend.ui.diet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.*
import com.dogukanpayal.victus_frontend.data.repository.DietPlanRepository
import com.dogukanpayal.victus_frontend.data.repository.DietPlanRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class DietPlanCreatorUiState(
    val planTitle: String = "",
    val startDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val durationDays: Int = 7,
    val selectedDayIndex: Int = 0,
    val days: List<DayFormState> = generateDays(7),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
)

fun generateDays(count: Int): List<DayFormState> {
    return (1..count).map { dayNum ->
        DayFormState(
            dayNumber = dayNum,
            dayLabel = "$dayNum. Gün",
            meals = listOf(
                MealFormEntry(mealType = "Kahvaltı"),
                MealFormEntry(mealType = "Öğle"),
                MealFormEntry(mealType = "Akşam")
            )
        )
    }
}

class DietPlanCreatorViewModel(
    context: Context,
    private val repository: DietPlanRepository = DietPlanRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietPlanCreatorUiState())
    val uiState: StateFlow<DietPlanCreatorUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(planTitle = title) }
    }

    fun updateStartDate(date: String) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun updateDurationDays(count: Int) {
        if (count < 1) return
        _uiState.update { state ->
            val currentDays = state.days
            val newDays = if (count > currentDays.size) {
                // Artır
                currentDays + (currentDays.size + 1..count).map { dayNum ->
                    DayFormState(
                        dayNumber = dayNum,
                        dayLabel = "$dayNum. Gün",
                        meals = listOf(
                            MealFormEntry(mealType = "Kahvaltı"),
                            MealFormEntry(mealType = "Öğle"),
                            MealFormEntry(mealType = "Akşam")
                        )
                    )
                }
            } else if (count < currentDays.size) {
                // Azalt
                currentDays.take(count)
            } else {
                currentDays
            }
            state.copy(
                durationDays = count,
                days = newDays,
                selectedDayIndex = state.selectedDayIndex.coerceAtMost(count - 1)
            )
        }
    }

    fun selectDay(index: Int) {
        _uiState.update { it.copy(selectedDayIndex = index) }
    }

    fun addMeal(dayIndex: Int) {
        _uiState.update { state ->
            val updatedDays = state.days.toMutableList()
            val day = updatedDays[dayIndex]
            updatedDays[dayIndex] = day.copy(
                meals = day.meals + MealFormEntry(mealType = "Ara Öğün")
            )
            state.copy(days = updatedDays)
        }
    }

    fun removeMeal(dayIndex: Int, mealId: String) {
        _uiState.update { state ->
            val updatedDays = state.days.toMutableList()
            val day = updatedDays[dayIndex]
            updatedDays[dayIndex] = day.copy(
                meals = day.meals.filter { it.id != mealId }
            )
            state.copy(days = updatedDays)
        }
    }

    fun updateMeal(dayIndex: Int, mealId: String, updatedMeal: MealFormEntry) {
        _uiState.update { state ->
            val updatedDays = state.days.toMutableList()
            val day = updatedDays[dayIndex]
            updatedDays[dayIndex] = day.copy(
                meals = day.meals.map { if (it.id == mealId) updatedMeal else it }
            )
            state.copy(days = updatedDays)
        }
    }

    fun applyToAllDays(sourceDayIndex: Int) {
        _uiState.update { state ->
            val sourceDay = state.days[sourceDayIndex]
            val newDays = state.days.map { day ->
                if (day.dayNumber == sourceDay.dayNumber) day
                else day.copy(
                    meals = sourceDay.meals.map { it.copy(id = UUID.randomUUID().toString()) }
                )
            }
            state.copy(days = newDays)
        }
    }

    fun moveMealUp(dayIndex: Int, mealId: String) {
        _uiState.update { state ->
            val updatedDays = state.days.toMutableList()
            val day = updatedDays[dayIndex]
            val meals = day.meals.toMutableList()
            val index = meals.indexOfFirst { it.id == mealId }
            if (index > 0) {
                val meal = meals.removeAt(index)
                meals.add(index - 1, meal)
                updatedDays[dayIndex] = day.copy(meals = meals)
            }
            state.copy(days = updatedDays)
        }
    }

    fun moveMealDown(dayIndex: Int, mealId: String) {
        _uiState.update { state ->
            val updatedDays = state.days.toMutableList()
            val day = updatedDays[dayIndex]
            val meals = day.meals.toMutableList()
            val index = meals.indexOfFirst { it.id == mealId }
            if (index != -1 && index < meals.size - 1) {
                val meal = meals.removeAt(index)
                meals.add(index + 1, meal)
                updatedDays[dayIndex] = day.copy(meals = meals)
            }
            state.copy(days = updatedDays)
        }
    }

    fun updateMealType(dayIndex: Int, mealId: String, newType: String) {
        _uiState.update { state ->
            val updatedDays = state.days.toMutableList()
            val day = updatedDays[dayIndex]
            val meals = day.meals.map { 
                if (it.id == mealId) it.copy(mealType = newType) else it 
            }
            updatedDays[dayIndex] = day.copy(meals = meals)
            state.copy(days = updatedDays)
        }
    }

    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        val state = _uiState.value
        if (state.planTitle.isBlank()) errors.add("Plan başlığı boş olamaz.")
        
        // En az bir öğün kontrolü
        val totalMeals = state.days.sumOf { it.meals.size }
        if (totalMeals == 0) errors.add("Planda en az bir öğün olmalıdır.")
        
        return errors
    }

    fun submitPlan(token: String) {
        val validationErrors = validateForm()
        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(submitError = validationErrors.joinToString("\n")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            
            val state = _uiState.value
            val requestItems = state.days.flatMap { day ->
                day.meals.mapIndexed { index, meal ->
                    CreateDietPlanItemRequest(
                        dayNumber = day.dayNumber,
                        mealIndex = index + 1,
                        foodName = meal.description.ifBlank { meal.mealType },
                        mealType = meal.mealType,
                        targetCalories = meal.targetCalories.toDoubleOrNull() ?: 0.0,
                        targetProtein = meal.targetProtein.toDoubleOrNull() ?: 0.0,
                        targetCarbs = meal.targetCarbs.toDoubleOrNull() ?: 0.0,
                        targetFat = meal.targetFat.toDoubleOrNull() ?: 0.0
                    )
                }
            }
            
            val request = CreateDietPlanRequest(
                title = state.planTitle,
                durationDays = state.durationDays,
                isActive = true,
                startDate = state.startDate,
                items = requestItems
            )
            
            val result = repository.createDietPlan(token, request)
            result.onSuccess {
                _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, submitError = error.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(submitError = null) }
    }

    fun resetForm() {
        _uiState.value = DietPlanCreatorUiState()
    }
}
