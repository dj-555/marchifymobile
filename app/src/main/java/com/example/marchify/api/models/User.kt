package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    val nom: String,
    val prenom: String,
    val email: String,
    val role: UserRole,
    val telephone: String,
    val adresse: Adresse,
    val localisation: Localisation? = null,
    val vendeur: Vendeur? = null,
    val livreur: Livreur? = null
)

data class Adresse(
    val rue: String,
    val ville: String,
    val codePostal: String,
    val pays: String = "Tunisie"
)

data class Localisation(
    val latitude: Double,
    val longitude: Double
)

// For vendeur-specific data
data class Vendeur(
    @SerializedName("_id")
    val id: String,
    val userId: String
)

// For livreur-specific data
data class Livreur(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val localisation: Localisation? = null
)
