package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Boutique
import com.example.marchify.api.models.Commande
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.OrderRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Vendeur Dashboard
 */
class DashboardViewModel(
    private val boutiqueRepository: BoutiqueRepository,
    private val orderRepository: OrderRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val vendeurId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load boutiques
            boutiqueRepository.getBoutiquesByVendeurId(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            boutiques = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> {}
                }
            }

            // Load recent orders
            orderRepository.getVendeurOrders(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val orders = result.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            recentOrders = orders.take(5),
                            pendingOrders = orders.count { it.status == CmdStatus.PENDING },
                            processingOrders = orders.count { it.status == CmdStatus.PROCESSING },
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun refreshDashboard() {
        loadDashboardData()
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val boutiques: List<Boutique> = emptyList(),
    val recentOrders: List<Commande> = emptyList(),
    val pendingOrders: Int = 0,
    val processingOrders: Int = 0,
    val errorMessage: String? = null
)
