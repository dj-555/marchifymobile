package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

// Response wrappers
data class MissionsResponse(val missions: List<Mission>)
data class MissionResponse(val mission: Mission)

/**
 * Mission represents a BonDeLivraison with included commande data
 */
data class Mission(
    @SerializedName("id")
    val id: String,
    val dateCreation: String,
    val commandeId: String,
    val status: DeliveryStatus,
    val livreurId: String? = null,
    val livreur: Livreur? = null,
    val commande: Commande  // ← Contains client, boutique, adresseLivraison, totalCommande
)

// Response after accepting mission
data class AcceptMissionResponse(
    val message: String,
    val bonDeLivraison: BonDeLivraison
)
