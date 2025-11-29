package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Boutique
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Boutiques List Screen
 */
class BoutiquesViewModel(
    private val boutiqueRepository: BoutiqueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoutiquesUiState())
    val uiState: StateFlow<BoutiquesUiState> = _uiState.asStateFlow()

    init {
        loadBoutiques()
    }

    fun loadBoutiques() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            boutiqueRepository.getAllBoutiques().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            boutiques = result.data ?: emptyList(),
                            filteredBoutiques = result.data ?: emptyList(),
                            isLoading = false
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
        }
    }

    fun searchBoutiques(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        val filtered = if (query.isBlank()) {
            _uiState.value.boutiques
        } else {
            _uiState.value.boutiques.filter { boutique ->
                boutique.nom.contains(query, ignoreCase = true) ||
                        boutique.adresse.contains(query, ignoreCase = true) ||
                        boutique.adresse.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(filteredBoutiques = filtered)
    }
}

data class BoutiquesUiState(
    val isLoading: Boolean = false,
    val boutiques: List<Boutique> = emptyList(),
    val filteredBoutiques: List<Boutique> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)
