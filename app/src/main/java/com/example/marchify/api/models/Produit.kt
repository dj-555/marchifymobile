package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class Produit(
    @SerializedName("id")
    val id: String,
    val nom: String,
    val prix: Double,
    val categorie: String,
    val description: String,
    val image: String,  // Cloudinary URL
    val quantite: Int,
    val unite: UniteMesure = UniteMesure.KILOGRAMME,
    val unitePersonnalisee: String? = null,
    val livrable: Boolean = true,
    val Ispinned: Boolean = false,
    val boutiqueId: String? = null,
    val boutique: Boutique? = null,
    val averageRating: Double? = null,
    val totalReviews: Int? = null
)

// For creating product (multipart form data)
data class ProduitRequest(
    val nom: String,
    val prix: Double,
    val categorie: String,
    val description: String,
    val quantite: Int,
    val unite: UniteMesure,
    val unitePersonnalisee: String? = null,
    val livrable: Boolean = true,
    val boutiqueId: String
)
