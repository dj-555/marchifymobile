package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id")
    val id: String,
    val quantite: Int,
    val prixTotal: Double,
    val dateAjout: String,
    val dateMaj: String,
    val panierId: String,
    val produitId: String,
    val produit: Produit  // Full product details
)

// Request models
data class AddToCartRequest(
    val clientId: String,
    val produitId: String,
    val quantite: Int
)

data class UpdateCartRequest(
    val clientId: String,
    val items: List<CartItemUpdate>
)

data class CartItemUpdate(
    val produitId: String,
    val quantite: Int
)

data class RemoveFromCartRequest(
    val clientId: String,
    val produitId: String
)

data class ConfirmOrderRequest(
    val clientId: String,
    val adresseLivraison: Adresse
)

data class ConfirmOrderResponse(
    val commandes: List<Commande>,
    val message: String
)
