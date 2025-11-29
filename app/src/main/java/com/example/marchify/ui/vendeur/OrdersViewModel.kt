package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.api.models.Commande
import com.example.marchify.data.repository.OrderRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Vendeur Orders Screen
 */
class OrdersViewModel(
    private val orderRepository: OrderRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        val vendeurId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            orderRepository.getVendeurOrders(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val orders = result.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            orders = orders,
                            filteredOrders = orders,
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

    fun filterOrders(status: CmdStatus?) {
        _uiState.value = _uiState.value.copy(selectedFilter = status)

        val filtered = if (status == null) {
            _uiState.value.orders
        } else {
            _uiState.value.orders.filter { it.status == status }
        }

        _uiState.value = _uiState.value.copy(filteredOrders = filtered)
    }
}

data class OrdersUiState(
    val isLoading: Boolean = false,
    val orders: List<Commande> = emptyList(),
    val filteredOrders: List<Commande> = emptyList(),
    val selectedFilter: CmdStatus? = null,
    val errorMessage: String? = null
)
