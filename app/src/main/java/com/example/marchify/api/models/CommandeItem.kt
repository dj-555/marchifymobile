package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class CommandeItem(
    @SerializedName("_id")
    val id: String,
    val quantite: Int,
    val prixTotal: Double,
    val dateAjout: String,
    val dateModif: String,
    val commandeId: String,
    val produitId: String,
    val produit: Produit? = null,
    val boutiqueId: String
)
