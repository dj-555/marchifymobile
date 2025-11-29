package com.example.marchify.data.repository

import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    /**
     * Login user
     */
    fun login(email: String, password: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val user = loginResponse.user

                // Save ALL user data to PrefsManager
                prefsManager.saveAuthToken(loginResponse.token)
                prefsManager.saveUserData(
                    userId = user.id,
                    role = user.role.name,
                    name = "${user.prenom} ${user.nom}",
                    email = user.email,
                    telephone = user.telephone,
                    adresse = user.adresse,
                    vendeurId = user.vendeurId,
                    livreurId = user.livreurId
                )

                emit(Resource.Success(loginResponse))
            } else {
                emit(Resource.Error(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }

    /**
     * Register new user
     */
    fun register(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        role: UserRole,
        telephone: String,
        adresse: String // Now STRING
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            val request = RegisterRequest(nom, prenom, email, password, role, telephone, adresse)
            val response = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get current user profile from PrefsManager
     */
    fun getProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            val userId = prefsManager.getUserId()
            val userEmail = prefsManager.getUserEmail()
            val userRole = prefsManager.getUserRole()
            val userName = prefsManager.getUserName()
            val telephone = prefsManager.getUserTelephone()
            val adresse = prefsManager.getUserAdresse()

            if (userId == null || userEmail == null || userRole == null) {
                emit(Resource.Error("No user data found. Please login again."))
                return@flow
            }

            // Parse name
            val nameParts = userName?.split(" ", limit = 2) ?: listOf("", "")
            val prenom = nameParts.getOrNull(0) ?: ""
            val nom = nameParts.getOrNull(1) ?: ""

            // Parse adresse string into Adresse object
            val adresseObj = if (adresse != null) {
                // Simple parsing - you might want to improve this
                Adresse(
                    rue = adresse,
                    ville = "",
                    codePostal = "",
                    pays = "Tunisie"
                )
            } else {
                Adresse("", "", "", "Tunisie")
            }

            val user = User(
                id = userId,
                nom = nom,
                prenom = prenom,
                email = userEmail,
                role = UserRole.valueOf(userRole),
                telephone = telephone ?: "",
                adresse = adresseObj
            )

            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Logout
     */
    fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            apiService.logout()
            prefsManager.clearAuth()
            RetrofitClient.clearInstance()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            prefsManager.clearAuth()
            emit(Resource.Success(true))
        }
    }

    fun getVendeurByUserId(userId: String): Flow<Resource<Vendeur>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getVendeurByUserId(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to get vendeur"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    fun getLivreurByUserId(userId: String): Flow<Resource<Livreur>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getLivreurByUserId(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to get livreur"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
