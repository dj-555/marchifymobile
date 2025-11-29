package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Boutique
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for My Boutiques Screen
 */
class MyBoutiquesViewModel(
    private val boutiqueRepository: BoutiqueRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyBoutiquesUiState())
    val uiState: StateFlow<MyBoutiquesUiState> = _uiState.asStateFlow()

    init {
        loadMyBoutiques()
    }

    fun loadMyBoutiques() {
        val vendeurId = prefsManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            boutiqueRepository.getBoutiquesByVendeurId(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            boutiques = result.data ?: emptyList(),
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
}

data class MyBoutiquesUiState(
    val isLoading: Boolean = false,
    val boutiques: List<Boutique> = emptyList(),
    val errorMessage: String? = null
)
