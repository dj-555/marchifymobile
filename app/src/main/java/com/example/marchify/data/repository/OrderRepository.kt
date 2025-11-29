package com.example.marchify.data.repository

import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for order operations
 */
class OrderRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    /**
     * Get order by ID (uses getDetailCommande)
     */
    fun getOrderById(orderId: String): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getDetailCommande(orderId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Commande not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get vendeur's orders
     */
    fun getVendeurOrders(vendeurId: String): Flow<Resource<List<Commande>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getCommandesVendeur(vendeurId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load orders"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get boutique's orders
     */
    fun getBoutiqueOrders(boutiqueId: String): Flow<Resource<List<Commande>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getCommandesBoutique(boutiqueId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load orders"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get client's orders
     */
    fun getClientOrders(clientId: String): Flow<Resource<List<Commande>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getCommandesByAcheteur(clientId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load orders"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
    /**
     * Get current user's orders (uses PrefsManager to get clientId)
     */
    fun getMyOrders(): Flow<Resource<List<Commande>>> = flow {
        emit(Resource.Loading())

        try {
            val clientId = prefsManager.getUserId()

            if (clientId == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            val response = apiService.getCommandesByAcheteur(clientId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load orders"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Update order status (generic)
     */
    fun updateOrderStatus(orderId: String, newStatus: CmdStatus): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val request = UpdateStatusRequest(status = newStatus)
            val response = apiService.updateCommandeStatus(orderId, request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to update status"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Accept order (PENDING -> PROCESSING)
     */
    fun acceptOrder(orderId: String): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.accepterCommande(orderId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to accept order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Prepare order (PROCESSING -> READY)
     */
    fun prepareOrder(orderId: String): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.preparerCommande(orderId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to prepare order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Ship order (READY -> SHIPPED)
     */
    fun shipOrder(orderId: String): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.expedierCommande(orderId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to ship order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Deliver order (SHIPPED -> DELIVERED)
     */
    fun deliverOrder(orderId: String): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.livrerCommande(orderId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to deliver order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Cancel order
     */
    fun cancelOrder(orderId: String): Flow<Resource<Commande>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.annulerCommande(orderId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to cancel order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get monthly stats
     */
    fun getMonthlyStats(vendeurId: String): Flow<Resource<MonthlyStats>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getStatsByMonth(vendeurId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load stats"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get status stats
     */
    fun getStatusStats(vendeurId: String): Flow<Resource<StatusStats>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getStatsByStatusForMonth(vendeurId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load status stats"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get stats for specific month and year
     */
    fun getStatsForMonthAndYear(
        vendeurId: String,
        month: Int,
        year: Int
    ): Flow<Resource<MonthlyStats>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getStatsByMonthAndYear(vendeurId, month, year)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load stats"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
