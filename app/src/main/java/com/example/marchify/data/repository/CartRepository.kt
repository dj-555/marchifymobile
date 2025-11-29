package com.example.marchify.data.repository

import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for cart operations
 */
class CartRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    /**
     * Get user's cart
     */
    fun getCart(clientId: String): Flow<Resource<Cart>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getCart(clientId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Add product to cart
     */
    fun addToCart(clientId: String, produitId: String, quantite: Int): Flow<Resource<Cart>> = flow {
        emit(Resource.Loading())

        try {
            val request = AddToCartRequest(clientId, produitId, quantite)
            val response = apiService.addToCart(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.panier))
            } else {
                emit(Resource.Error("Failed to add to cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Update cart quantities
     */
    fun updateCart(clientId: String, items: List<CartItemUpdate>): Flow<Resource<Cart>> = flow {
        emit(Resource.Loading())

        try {
            val request = UpdateCartRequest(clientId, items)
            val response = apiService.updateCartQuantities(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to update cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Remove item from cart
     */
    fun removeFromCart(clientId: String, produitId: String): Flow<Resource<Cart>> = flow {
        emit(Resource.Loading())

        try {
            val request = RemoveFromCartRequest(clientId, produitId)
            val response = apiService.removeFromCart(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to remove item"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Clear entire cart
     */
    fun clearCart(clientId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.clearCart(clientId)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to clear cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Confirm order (checkout)
     */
    fun confirmOrder(clientId: String, adresseLivraison: Adresse): Flow<Resource<ConfirmOrderResponse>> = flow {
        emit(Resource.Loading())

        try {
            val request = ConfirmOrderRequest(clientId, adresseLivraison)
            val response = apiService.confirmOrder(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to place order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Recalculate cart total
     */
    fun recalcCartTotal(clientId: String): Flow<Resource<Cart>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.recalcCartTotal(clientId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to recalculate total"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
