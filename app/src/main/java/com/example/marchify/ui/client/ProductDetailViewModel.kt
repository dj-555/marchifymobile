package com.example.marchify.ui.client

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marchify.api.models.Produit
import com.example.marchify.data.repository.CartRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Product Detail Screen
 */
class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val prefsManager: PrefsManager // ADD THIS
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()


    fun loadProduct(productId: String) {
        Log.d("ProductDetailVM", "loadProduct called with productId: $productId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            productRepository.getProductById(productId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("ProductDetailVM", "Product loaded successfully: ${result.data?.nom}")
                        _uiState.value = _uiState.value.copy(
                            product = result.data,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        Log.e("ProductDetailVM", "Error loading product: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message ?: "Erreur de chargement",
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        Log.d("ProductDetailVM", "Loading product...")
                    }
                }
            }
        }
    }

    fun updateQuantity(quantity: Int) {
        if (quantity > 0) {
            _uiState.value = _uiState.value.copy(quantity = quantity)
        }
    }

    fun incrementQuantity() {
        _uiState.value = _uiState.value.copy(quantity = _uiState.value.quantity + 1)
    }

    fun decrementQuantity() {
        if (_uiState.value.quantity > 1) {
            _uiState.value = _uiState.value.copy(quantity = _uiState.value.quantity - 1)
        }
    }

    fun addToCart() {
        val product = _uiState.value.product ?: return
        val clientId = prefsManager.getUserId()

        if (clientId == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Veuillez vous connecter pour ajouter au panier"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToCart = true)

            cartRepository.addToCart(
                clientId = clientId,
                produitId = product.id,
                quantite = _uiState.value.quantity
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isAddingToCart = false,
                            showAddedToCartMessage = true
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isAddingToCart = false,
                            errorMessage = result.message ?: "Erreur"
                        )
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearAddedMessage() {
        _uiState.value = _uiState.value.copy(showAddedToCartMessage = false)
    }
}

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Produit? = null,
    val quantity: Int = 1,
    val isAddingToCart: Boolean = false,
    val showAddedToCartMessage: Boolean = false,
    val errorMessage: String? = null
)
