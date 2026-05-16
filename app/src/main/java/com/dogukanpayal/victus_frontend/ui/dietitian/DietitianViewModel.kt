package com.dogukanpayal.victus_frontend.ui.dietitian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogukanpayal.victus_frontend.data.model.PatientDetail
import com.dogukanpayal.victus_frontend.data.model.PatientSummary
import com.dogukanpayal.victus_frontend.data.repository.DietitianRepository
import com.dogukanpayal.victus_frontend.data.repository.MockDietitianRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DietitianUiState(
    val patients: List<PatientSummary> = emptyList(),
    val filteredPatients: List<PatientSummary> = emptyList(),
    val selectedPatient: PatientDetail? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null
)

class DietitianViewModel(
    private val repository: DietitianRepository = MockDietitianRepositoryImpl()
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
            _uiState.update { it.copy(isLoading = true, error = null, selectedPatient = null) }
            val result = repository.getPatientDetail(token, patientId)
            result.onSuccess { detail ->
                _uiState.update { it.copy(selectedPatient = detail, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
