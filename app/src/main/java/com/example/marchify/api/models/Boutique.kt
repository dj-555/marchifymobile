package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class Boutique(
    @SerializedName("id")
    val id: String,
    val nom: String,
    val adresse: String,
    val localisation: Localisation? = null,
    val categorie: String,
    val telephone: String,
    val vendeurId: String,
    val vendeur: Vendeur? = null,
    val produits: List<Produit>? = null,
    val averageRating: Double? = null,
    val totalReviews: Int? = null
)

// For creating/updating boutique
data class BoutiqueRequest(
    val nom: String,
    val adresse: String,
    val localisation: Localisation? = null,
    val categorie: String,
    val telephone: String,
    val vendeurId: String
)
