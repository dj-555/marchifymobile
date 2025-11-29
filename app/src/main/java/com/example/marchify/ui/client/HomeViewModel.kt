package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Boutique
import com.example.marchify.api.models.Produit
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.data.repository.CartRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Home Screen
 * Handles featured products, boutiques, and cart operations
 */
class HomeViewModel(
    private val boutiqueRepository: BoutiqueRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val prefsManager: PrefsManager // ADD THIS
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load featured products
            productRepository.getFeaturedProducts().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            featuredProducts = result.data ?: emptyList(),
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

            // Load cart count
            loadCartCount()
        }
    }

    fun loadCartCount() {
        viewModelScope.launch {
            val clientId = prefsManager.getUserId()

            if (clientId == null) {
                _uiState.value = _uiState.value.copy(cartItemCount = 0)
                return@launch
            }

            cartRepository.getCart(clientId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val count = result.data?.produits?.sumOf { it.quantite } ?: 0
                        _uiState.value = _uiState.value.copy(cartItemCount = count)
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(cartItemCount = 0)
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            val clientId = prefsManager.getUserId()

            if (clientId == null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Veuillez vous connecter"
                )
                return@launch
            }

            cartRepository.addToCart(
                clientId = clientId,
                produitId = productId,
                quantite = quantity
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadCartCount()
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

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredProducts: List<Produit> = emptyList(),
    val popularBoutiques: List<Boutique> = emptyList(),
    val cartItemCount: Int = 0,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null
)
