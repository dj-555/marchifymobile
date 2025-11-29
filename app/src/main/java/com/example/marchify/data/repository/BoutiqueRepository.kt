package com.example.marchify.data.repository

import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.Boutique
import com.example.marchify.api.models.BoutiqueRequest
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for boutique operations
 */
class BoutiqueRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    /**
     * Get all boutiques
     */
    fun getAllBoutiques(): Flow<Resource<List<Boutique>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getBoutiques()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load boutiques"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get boutique by ID
     */
    fun getBoutiqueById(id: String): Flow<Resource<Boutique>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getBoutiqueById(id)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Boutique not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get boutiques by vendeur ID
     */
    fun getBoutiquesByVendeurId(vendeurId: String): Flow<Resource<List<Boutique>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getBoutiquesByVendeurId(vendeurId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load your boutiques"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Create new boutique
     */
    fun createBoutique(request: BoutiqueRequest): Flow<Resource<Boutique>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.createBoutique(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to create boutique"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Update boutique
     */
    fun updateBoutique(id: String, request: BoutiqueRequest): Flow<Resource<Boutique>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.updateBoutique(id, request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to update boutique"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
