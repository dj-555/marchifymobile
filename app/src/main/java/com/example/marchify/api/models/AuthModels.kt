package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val nom: String,
    val prenom: String,
    val email: String,
    @SerializedName("PWD")
    val motDePasse: String,
    val role: UserRole,
    val telephone: String,
    val adresse: String // Backend expects STRING
)

data class LoginRequest(
    val email: String,
    @SerializedName("PWD")
    val motDePasse: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserLoginResponse
)

// Separate model for login response (matches backend exactly)
data class UserLoginResponse(
    @SerializedName("id")
    val id: String,
    val nom: String,
    val prenom: String,
    val email: String,
    val role: UserRole,
    val telephone: String,
    val adresse: Adresse, // Backend returns STRING
    val vendeurId: String? = null,
    val livreurId: String? = null
)

