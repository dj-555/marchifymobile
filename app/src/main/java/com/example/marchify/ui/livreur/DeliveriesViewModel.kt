package com.example.marchify.ui.livreur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.BonDeLivraison
import com.example.marchify.api.models.DeliveryStatus
import com.example.marchify.data.repository.MissionRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Deliveries Screen
 * Shows livreur's accepted deliveries
 */
class DeliveriesViewModel(
    private val missionRepository: MissionRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveriesUiState())
    val uiState: StateFlow<DeliveriesUiState> = _uiState.asStateFlow()

    init {
        loadMyDeliveries()
    }

    fun loadMyDeliveries() {
        val livreurId = prefsManager.getLivreurId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            missionRepository.getLivreurBons(livreurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val deliveries = result.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            deliveries = deliveries,
                            filteredDeliveries = deliveries,
                            isLoading = false
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

    fun filterDeliveries(status: DeliveryStatus?) {
        _uiState.value = _uiState.value.copy(selectedFilter = status)

        val filtered = if (status == null) {
            _uiState.value.deliveries
        } else {
            _uiState.value.deliveries.filter { it.status == status }
        }

        _uiState.value = _uiState.value.copy(filteredDeliveries = filtered)
    }

    fun pickupOrder(bonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            missionRepository.pickupOrder(bonId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            updateSuccess = true
                        )
                        loadMyDeliveries() // Refresh
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun deliverOrder(bonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            missionRepository.deliverOrder(bonId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            updateSuccess = true
                        )
                        loadMyDeliveries() // Refresh
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun failDelivery(bonId: String, raison: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            missionRepository.failDelivery(bonId, raison).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            updateSuccess = true
                        )
                        loadMyDeliveries() // Refresh
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearUpdateSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }
}

data class DeliveriesUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val deliveries: List<BonDeLivraison> = emptyList(),
    val filteredDeliveries: List<BonDeLivraison> = emptyList(),
    val selectedFilter: DeliveryStatus? = null,
    val updateSuccess: Boolean = false,
    val errorMessage: String? = null
)
