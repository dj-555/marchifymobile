package com.example.marchify.ui.livreur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Mission
import com.example.marchify.api.models.AcceptMissionResponse
import com.example.marchify.data.repository.MissionRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Mission Detail Screen
 * Shows mission details and accept/reject options
 */
class MissionDetailViewModel(
    private val missionRepository: MissionRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionDetailUiState())
    val uiState: StateFlow<MissionDetailUiState> = _uiState.asStateFlow()

    fun loadMissionDetail(missionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                missionRepository.getMissionById(missionId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                mission = result.data,
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

    fun acceptMission(bonDeLivraisonId: String) {
        val livreurId = prefsManager.getLivreurId()

        if (livreurId == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "User not logged in"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAccepting = true, errorMessage = null)

            try {
                missionRepository.acceptMission(livreurId, bonDeLivraisonId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isAccepting = false,
                                isAccepted = true,
                                acceptedResponse = result.data,
                                errorMessage = null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isAccepting = false,
                                errorMessage = result.message ?: "Erreur"
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isAccepting = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAccepting = false,
                    errorMessage = "Erreur: ${e.localizedMessage ?: "Erreur inconnue"}"
                )
            }
        }
    }

    fun refuseMission(commandeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(errorMessage = null)

            try {
                missionRepository.refuseMission(commandeId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isRefused = true,
                                errorMessage = null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = result.message ?: "Erreur"
                            )
                        }
                        is Resource.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erreur: ${e.localizedMessage ?: "Erreur inconnue"}"
                )
            }
        }
    }
}

data class MissionDetailUiState(
    val isLoading: Boolean = false,
    val mission: Mission? = null,
    val isAccepting: Boolean = false,
    val isAccepted: Boolean = false,
    val isRefused: Boolean = false,
    val acceptedResponse: AcceptMissionResponse? = null,
    val errorMessage: String? = null
)
