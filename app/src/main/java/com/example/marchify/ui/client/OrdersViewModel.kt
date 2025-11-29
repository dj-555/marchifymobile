package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Commande
import com.example.marchify.api.models.CmdStatus
import com.example.marchify.data.repository.OrderRepository
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Orders List Screen
 */
class OrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            orderRepository.getMyOrders().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            orders = result.data ?: emptyList(),
                            filteredOrders = result.data ?: emptyList(),
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

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDetail = true)

            orderRepository.getOrderById(orderId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            selectedOrder = result.data,
                            isLoadingDetail = false
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message,
                            isLoadingDetail = false
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}

data class OrdersUiState(
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val orders: List<Commande> = emptyList(),
    val filteredOrders: List<Commande> = emptyList(),
    val selectedOrder: Commande? = null,
    val selectedFilter: CmdStatus? = null,
    val errorMessage: String? = null
)
