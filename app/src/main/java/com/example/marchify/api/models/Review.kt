package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("_id")
    val id: String,
    val type: ReviewType,
    val rating: Int,  // 1-5 stars
    val comment: String? = null,
    val createdAt: String,
    val auteurId: String,
    val auteur: User? = null,
    val produitId: String? = null,
    val produit: Produit? = null,
    val boutiqueId: String? = null,
    val boutique: Boutique? = null
)

// Request model
data class ReviewRequest(
    val type: ReviewType,
    val rating: Int,
    val comment: String? = null,
    val auteurId: String,
    val produitId: String? = null,
    val boutiqueId: String? = null
)
