package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.MonthlyStats
import com.example.marchify.api.models.StatusStats
import com.example.marchify.data.repository.OrderRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for Analytics Screen
 */
class AnalyticsViewModel(
    private val orderRepository: OrderRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        val vendeurId = prefsManager.getVendeurId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Load monthly stats
            orderRepository.getMonthlyStats(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            monthlyStats = result.data
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

            // Load status stats
            orderRepository.getStatusStats(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            statusStats = result.data,
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

    fun loadStatsForMonth(month: Int, year: Int) {
        val vendeurId = prefsManager.getVendeurId() ?: return

        viewModelScope.launch {
            orderRepository.getStatsForMonthAndYear(vendeurId, month, year).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            monthlyStats = result.data
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
        }
    }

    fun refreshAnalytics() {
        loadAnalytics()
    }
}

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val monthlyStats: MonthlyStats? = null,
    val statusStats: StatusStats? = null,
    val errorMessage: String? = null
)
