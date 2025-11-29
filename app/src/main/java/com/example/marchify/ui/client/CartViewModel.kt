package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Adresse
import com.example.marchify.api.models.Cart
import com.example.marchify.data.repository.CartRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Shopping Cart Screen
 */
class CartViewModel(
    private val cartRepository: CartRepository,
    private val prefsManager: PrefsManager // ADD THIS
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val clientId = prefsManager.getUserId()
            if (clientId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Utilisateur non connecté"
                )
                return@launch
            }

            cartRepository.getCart(clientId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            cart = result.data,
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

    fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            val clientId = prefsManager.getUserId() ?: return@launch

            // You need to implement updateCartItem in CartRepository or use updateCart
            // For now, I'll show you how to use updateCart:
            val items = listOf(
                com.example.marchify.api.models.CartItemUpdate(
                    produitId = productId,
                    quantite = quantity
                )
            )

            cartRepository.updateCart(clientId, items).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadCart()
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

    fun removeItem(productId: String) {
        viewModelScope.launch {
            val clientId = prefsManager.getUserId() ?: return@launch

            cartRepository.removeFromCart(clientId, productId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadCart()
                        _uiState.value = _uiState.value.copy(
                            snackbarMessage = "Produit retiré du panier"
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

    fun clearCart() {
        viewModelScope.launch {
            val clientId = prefsManager.getUserId() ?: return@launch

            cartRepository.clearCart(clientId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadCart()
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun confirmOrder(adresse: Adresse) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val clientId = prefsManager.getUserId()
            if (clientId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Utilisateur non connecté"
                )
                return@launch
            }

            cartRepository.confirmOrder(clientId, adresse).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            orderPlaced = true,
                            cart = null // Clear cart after successful order
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Erreur lors de la commande"
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

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    val orderPlaced: Boolean = false // ADD THIS
)
