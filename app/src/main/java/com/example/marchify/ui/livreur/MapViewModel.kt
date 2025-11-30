package com.example.marchify.ui.livreur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.BonDeLivraison
import com.example.marchify.data.repository.MissionRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Map Screen
 * Handles location and route data
 */
class MapViewModel(
    private val missionRepository: MissionRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun loadDeliveryRoute(deliveryId: String) {
        val livreurId = prefsManager.getLivreurId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            missionRepository.getLivreurBons(livreurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val delivery = result.data?.find { it.id == deliveryId }
                        _uiState.value = _uiState.value.copy(
                            delivery = delivery,
                            isLoading = false,
                            errorMessage = if (delivery == null) "Livraison introuvable" else null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message ?: "Erreur de chargement",
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun updateCurrentLocation(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(
            currentLat = latitude,
            currentLng = longitude
        )
    }
}

data class MapUiState(
    val isLoading: Boolean = false,
    val delivery: BonDeLivraison? = null,
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val errorMessage: String? = null
)
