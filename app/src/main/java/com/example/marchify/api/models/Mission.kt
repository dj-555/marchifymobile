package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

/**
 * Mission represents an available delivery task for livreurs
 * This is essentially a BonDeLivraison with additional computed fields
 */
data class Mission(
    @SerializedName("id")
    val id: String,
    val bonDeLivraisonId: String,
    val commandeId: String,
    val dateCreation: String,
    val status: DeliveryStatus,
    val adresseLivraison: Adresse,
    val boutique: Boutique,
    val client: User,
    val totalCommande: Double,
    val distance: Double? = null,  // Calculated distance in km
    val estimatedTime: Int? = null  // Estimated delivery time in minutes
)

// Response after accepting mission
data class AcceptMissionResponse(
    val mission: Mission,
    val bonDeLivraison: BonDeLivraison,
    val message: String
)
