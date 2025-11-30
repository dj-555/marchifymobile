package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class CommandesResponse(
    val commandes: List<Commande>
)

data class CommandeResponse(
    val commande: Commande

)
data class Commande(
    @SerializedName("id")
    val id: String,
    val status: CmdStatus = CmdStatus.PENDING,
    val adresseLivraison: Adresse,
    val totalCommande: Double,
    val dateCommande: String,  // ISO date string
    val clientId: String,
    val client: User? = null,
    val boutiqueId: String? = null,
    val boutique: Boutique? = null,
    val produits: List<CommandeItem> = emptyList(),
    val bonDeLivraison: BonDeLivraison? = null
)

// Request models
data class UpdateStatusRequest(
    val status: CmdStatus
)
