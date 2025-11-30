package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class BonDeLivraison(
    @SerializedName("id")
    val id: String,
    val dateCreation: String,  // ISO date string
    val commandeId: String,
    val commande: Commande? = null,
    val status: DeliveryStatus = DeliveryStatus.PENDING_PICKUP,
    val livreurId: String? = null,
    val livreur: Livreur? = null
)

// Response wrapper for getBonsDeLivraisonByLivreur
data class BonsLivraisonResponse(
    val bons: List<BonDeLivraison>
)

// Request models
data class AssignLivreurRequest(
    val livreurId: String
)

data class FailDeliveryRequest(
    val raison: String
)
