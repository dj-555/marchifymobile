package com.example.marchify.data.repository

import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for livreur mission operations
 */
class MissionRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    /**
     * Get available missions
     */
    fun getAvailableMissions(): Flow<Resource<List<Mission>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getMissionsDisponibles()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                emit(Resource.Success(body.missions)) // ← unwrap missions
            } else {
                emit(Resource.Error("Failed to load missions"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get mission by ID
     */
    fun getMissionById(id: String): Flow<Resource<Mission>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getMissionById(id)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                emit(Resource.Success(body.mission)) // ← unwrap mission
            } else {
                emit(Resource.Error("Mission not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Accept mission
     */
    fun acceptMission(livreurId: String, bonId: String): Flow<Resource<AcceptMissionResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.accepterMission(livreurId, bonId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to accept mission"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Refuse mission
     */
    fun refuseMission(commandeId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.refuserMission(commandeId)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to refuse mission"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get livreur's assigned bons de livraison
     */
    /**
     * Get livreur's assigned bons de livraison
     */
    fun getLivreurBons(livreurId: String): Flow<Resource<List<BonDeLivraison>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getBonsDeLivraisonByLivreur(livreurId)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                emit(Resource.Success(body.bons))  // ← Unwrap the bons array
            } else {
                emit(Resource.Error("Failed to load deliveries"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }


    /**
     * Pickup order
     */
    fun pickupOrder(bonId: String): Flow<Resource<BonDeLivraison>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.pickupCommande(bonId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to pickup order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Deliver order
     */
    fun deliverOrder(bonId: String): Flow<Resource<BonDeLivraison>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.deliverCommande(bonId)

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
     * Mark delivery as failed
     */
    fun failDelivery(bonId: String, raison: String): Flow<Resource<BonDeLivraison>> = flow {
        emit(Resource.Loading())

        try {
            val request = FailDeliveryRequest(raison)
            val response = apiService.failDelivery(bonId, request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to update status"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
