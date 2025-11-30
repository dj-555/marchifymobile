package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Boutique
import com.example.marchify.api.models.Produit
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for My Products Screen
 */
class MyProductsViewModel(
    private val boutiqueRepository: BoutiqueRepository,
    private val productRepository: ProductRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyProductsUiState())
    val uiState: StateFlow<MyProductsUiState> = _uiState.asStateFlow()

    init {
        loadVendeurBoutiques()
    }

    private fun loadVendeurBoutiques() {
        val vendeurId = prefsManager.getVendeurId() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            boutiqueRepository.getBoutiquesByVendeurId(vendeurId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val boutiques = result.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            boutiques = boutiques,
                            isLoading = false
                        )

                        // Load products for first boutique if available
                        if (boutiques.isNotEmpty()) {
                            loadProductsForBoutique(boutiques.first().id)
                        }
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

    fun loadProductsForBoutique(boutiqueId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedBoutiqueId = boutiqueId,
                isLoadingProducts = true
            )

            productRepository.getProductsByShopId(boutiqueId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            products = result.data ?: emptyList(),
                            isLoadingProducts = false
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message,
                            isLoadingProducts = false
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Reload products
                        _uiState.value.selectedBoutiqueId?.let { boutiqueId ->
                            loadProductsForBoutique(boutiqueId)
                        }
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
}

data class MyProductsUiState(
    val isLoading: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val boutiques: List<Boutique> = emptyList(),
    val selectedBoutiqueId: String? = null,
    val products: List<Produit> = emptyList(),
    val errorMessage: String? = null
)
