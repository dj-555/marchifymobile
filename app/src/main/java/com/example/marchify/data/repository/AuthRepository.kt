package com.example.marchify.data.repository

import android.util.Log
import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.*
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)

    fun login(email: String?, password: String?): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())

        // Validate inputs upfront
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            emit(Resource.Error("Email and password must not be empty"))
            return@flow
        }

        try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val user = loginResponse.user

                Log.d("AuthRepo", "=== Login Success ===")
                Log.d("AuthRepo", "User ID: ${user.id}")
                Log.d("AuthRepo", "User Role: ${user.role.name}")
                Log.d("AuthRepo", "Email: ${user.email}")

                val userId: String = user.id
                val roleName: String = user.role.name
                val emailAddr: String = user.email
                val prenom = user.prenom ?: ""
                val nom = user.nom ?: ""

                if (userId.isBlank() || roleName.isBlank() || emailAddr.isBlank()) {
                    emit(Resource.Error("Invalid user data: missing critical fields"))
                    return@flow
                }

                // Save token first
                prefsManager.saveAuthToken(loginResponse.token)

                // Fetch and save vendeur/livreur ID based on role
                var vendeurId: String? = null
                var livreurId: String? = null

                when (user.role) {
                    UserRole.VENDEUR -> {
                        Log.d("AuthRepo", "Fetching Vendeur ID for user: $userId")
                        try {
                            val vendeurResponse = apiService.getVendeurByUserId(userId)
                            if (vendeurResponse.isSuccessful && vendeurResponse.body() != null) {
                                vendeurId = vendeurResponse.body()!!.id
                                Log.d("AuthRepo", "Vendeur ID fetched: $vendeurId")
                            } else {
                                Log.e("AuthRepo", "Failed to fetch vendeur ID")
                            }
                        } catch (e: Exception) {
                            Log.e("AuthRepo", "Error fetching vendeur ID: ${e.message}")
                        }
                    }
                    UserRole.LIVREUR -> {
                        Log.d("AuthRepo", "Fetching Livreur ID for user: $userId")
                        try {
                            val livreurResponse = apiService.getLivreurByUserId(userId)
                            if (livreurResponse.isSuccessful && livreurResponse.body() != null) {
                                livreurId = livreurResponse.body()!!.id
                                Log.d("AuthRepo", "Livreur ID fetched: $livreurId")
                            } else {
                                Log.e("AuthRepo", "Failed to fetch livreur ID")
                            }
                        } catch (e: Exception) {
                            Log.e("AuthRepo", "Error fetching livreur ID: ${e.message}")
                        }
                    }
                    else -> {
                        Log.d("AuthRepo", "Role is CLIENT or other, no additional ID needed")
                    }
                }

                // Save all user data
                prefsManager.saveUserData(
                    id = userId,
                    role = roleName,
                    name = "$prenom $nom".trim(),
                    email = emailAddr,
                    telephone = user.telephone ?: "",
                    adresse = user.adresse,
                    vendeurId = vendeurId ?: "",
                    livreurId = livreurId ?: ""
                )

                Log.d("AuthRepo", "User data saved - VendeurID: $vendeurId, LivreurID: $livreurId")
                Log.d("AuthRepo", "===================")

                emit(Resource.Success(loginResponse))
            } else {
                emit(Resource.Error(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }

    fun register(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        role: UserRole,
        telephone: String,
        adresse: String
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

    fun getProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            val userId = prefsManager.getUserId()
            val userEmail = prefsManager.getUserEmail()
            val userRole = prefsManager.getUserRole()
            val userName = prefsManager.getUserName()
            val telephone = prefsManager.getUserTelephone()
            val adresse = prefsManager.getUserAdresse() ?: Adresse("", "Tunis", "", "Tunisie")

            if (userId.isNullOrBlank() || userEmail.isNullOrBlank() || userRole.isNullOrBlank()) {
                emit(Resource.Error("No user data found. Please login again."))
                return@flow
            }

            val nameParts = userName?.split(" ", limit = 2) ?: listOf("", "")
            val prenom = nameParts.getOrNull(0) ?: ""
            val nom = nameParts.getOrNull(1) ?: ""

            val user = User(
                id = userId,
                nom = nom,
                prenom = prenom,
                email = userEmail,
                role = UserRole.valueOf(userRole),
                telephone = telephone ?: "",
                adresse = adresse
            )

            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

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
