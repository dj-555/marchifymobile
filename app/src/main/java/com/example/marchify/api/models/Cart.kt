package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("id")
    val id: String,
    val clientId: String,
    val produits: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val dateMaj: String  // ISO date string
)

// Response after adding to cart
data class AddToCartResponse(
    val panier: Cart,
    val message: String
)
