package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Boutique
import com.example.marchify.api.models.Produit
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.CartRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Products and Boutique Detail screens
 */
class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val boutiqueRepository: BoutiqueRepository,
    private val cartRepository: CartRepository,
    private val prefsManager: PrefsManager // ADD THIS
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    fun loadProductsByBoutique(boutiqueId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            productRepository.getProductsByShopId(boutiqueId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            products = result.data ?: emptyList(),
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

    fun loadBoutiqueDetails(boutiqueId: String) {
        viewModelScope.launch {
            boutiqueRepository.getBoutiqueById(boutiqueId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(boutique = result.data)
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            // Get current user's ID
            val clientId = prefsManager.getUserId()

            if (clientId == null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Erreur: Utilisateur non connecté"
                )
                return@launch
            }

            cartRepository.addToCart(clientId, productId, quantity).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            snackbarMessage = "Produit ajouté au panier"
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            snackbarMessage = result.message ?: "Erreur"
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}

data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Produit> = emptyList(),
    val boutique: Boutique? = null,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null
)
