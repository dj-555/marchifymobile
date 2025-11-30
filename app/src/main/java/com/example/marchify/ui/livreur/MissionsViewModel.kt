package com.example.marchify.ui.livreur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Mission
import com.example.marchify.data.repository.MissionRepository
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Missions Screen
 * Shows available delivery missions
 */
class MissionsViewModel(
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionsUiState())
    val uiState: StateFlow<MissionsUiState> = _uiState.asStateFlow()

    init {
        loadAvailableMissions()
    }

    fun loadAvailableMissions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                missionRepository.getAvailableMissions().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                missions = result.data ?: emptyList(),
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = result.message ?: "Erreur de chargement",
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erreur: ${e.localizedMessage ?: "Erreur inconnue"}",
                    isLoading = false
                )
            }
        }
    }

    fun refreshMissions() {
        loadAvailableMissions()
    }
}

data class MissionsUiState(
    val isLoading: Boolean = false,
    val missions: List<Mission> = emptyList(),
    val errorMessage: String? = null
)
