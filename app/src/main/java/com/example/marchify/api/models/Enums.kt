package com.example.marchify.api.models

import com.google.gson.annotations.SerializedName

enum class UserRole {
    @SerializedName("CLIENT")
    CLIENT,
    @SerializedName("VENDEUR")
    VENDEUR,
    @SerializedName("LIVREUR")
    LIVREUR,
    @SerializedName("ADMIN")
    ADMIN
}

enum class CmdStatus {
    @SerializedName("PENDING")
    PENDING,
    @SerializedName("PROCESSING")
    PROCESSING,
    @SerializedName("READY")
    READY,
    @SerializedName("SHIPPED")
    SHIPPED,
    @SerializedName("DELIVERED")
    DELIVERED,
    @SerializedName("CANCELLED")
    CANCELLED,
    @SerializedName("RETURNED")
    RETURNED
}

enum class UniteMesure {
    @SerializedName("GRAMME")
    GRAMME,
    @SerializedName("KILOGRAMME")
    KILOGRAMME,
    @SerializedName("LITRE")
    LITRE,
    @SerializedName("MILLILITRE")
    MILLILITRE,
    @SerializedName("PIECE")
    PIECE,
    @SerializedName("BOITE")
    BOITE,
    @SerializedName("SAC")
    SAC,
    @SerializedName("CARTON")
    CARTON,
    @SerializedName("METRE")
    METRE,
    @SerializedName("CENTIMETRE")
    CENTIMETRE,
    @SerializedName("AUTRE")
    AUTRE
}

enum class DeliveryStatus {
    @SerializedName("PENDING_PICKUP")
    PENDING_PICKUP,
    @SerializedName("IN_TRANSIT")
    IN_TRANSIT,
    @SerializedName("DELIVERED")
    DELIVERED,
    @SerializedName("FAILED")
    FAILED
}

enum class ReviewType {
    @SerializedName("PRODUIT")
    PRODUIT,
    @SerializedName("BOUTIQUE")
    BOUTIQUE
}

enum class NotificationType {
    @SerializedName("ORDER_PLACED")
    ORDER_PLACED,
    @SerializedName("NEW_ORDER_RECEIVED")
    NEW_ORDER_RECEIVED,
    @SerializedName("ORDER_CONFIRMED")
    ORDER_CONFIRMED,
    @SerializedName("ORDER_PROCESSING")
    ORDER_PROCESSING,
    @SerializedName("ORDER_READY")
    ORDER_READY,
    @SerializedName("ORDER_SHIPPED")
    ORDER_SHIPPED,
    @SerializedName("ORDER_DELIVERED")
    ORDER_DELIVERED,
    @SerializedName("ORDER_CANCELLED")
    ORDER_CANCELLED,
    @SerializedName("ORDER_RETURNED")
    ORDER_RETURNED,
    @SerializedName("REVIEW_RECEIVED")
    REVIEW_RECEIVED,
    @SerializedName("PRODUCT_LOW_STOCK")
    PRODUCT_LOW_STOCK,
    @SerializedName("PRODUCT_OUT_OF_STOCK")
    PRODUCT_OUT_OF_STOCK,
    @SerializedName("NEW_PRODUCT_ADDED")
    NEW_PRODUCT_ADDED,
    @SerializedName("DELIVERY_ASSIGNED")
    DELIVERY_ASSIGNED,
    @SerializedName("DELIVERY_PICKED_UP")
    DELIVERY_PICKED_UP,
    @SerializedName("DELIVERY_FAILED")
    DELIVERY_FAILED,
    @SerializedName("PROMO_ALERT")
    PROMO_ALERT,
    @SerializedName("SYSTEM_ANNOUNCEMENT")
    SYSTEM_ANNOUNCEMENT
}

enum class NotificationPriority {
    @SerializedName("LOW")
    LOW,
    @SerializedName("MEDIUM")
    MEDIUM,
    @SerializedName("HIGH")
    HIGH,
    @SerializedName("URGENT")
    URGENT
}

// Extension functions for CmdStatus (StatutCommande)
fun CmdStatus.toLabel(): String {
    return when (this) {
        CmdStatus.PENDING -> "En attente"
        CmdStatus.PROCESSING -> "En traitement"
        CmdStatus.READY -> "Prête"
        CmdStatus.SHIPPED -> "Expédiée"
        CmdStatus.DELIVERED -> "Livrée"
        CmdStatus.CANCELLED -> "Annulée"
        CmdStatus.RETURNED -> "Retournée"
    }
}

// Extension functions for DeliveryStatus
fun DeliveryStatus.toLabel(): String {
    return when (this) {
        DeliveryStatus.PENDING_PICKUP -> "En attente de récupération"
        DeliveryStatus.IN_TRANSIT -> "En transit"
        DeliveryStatus.DELIVERED -> "Livrée"
        DeliveryStatus.FAILED -> "Échec de livraison"
    }
}

// Extension functions for NotificationType
fun NotificationType.toLabel(): String {
    return when (this) {
        NotificationType.ORDER_PLACED -> "Commande passée"
        NotificationType.NEW_ORDER_RECEIVED -> "Nouvelle commande reçue"
        NotificationType.ORDER_CONFIRMED -> "Commande confirmée"
        NotificationType.ORDER_PROCESSING -> "Commande en traitement"
        NotificationType.ORDER_READY -> "Commande prête"
        NotificationType.ORDER_SHIPPED -> "Commande expédiée"
        NotificationType.ORDER_DELIVERED -> "Commande livrée"
        NotificationType.ORDER_CANCELLED -> "Commande annulée"
        NotificationType.ORDER_RETURNED -> "Commande retournée"
        NotificationType.REVIEW_RECEIVED -> "Nouvel avis reçu"
        NotificationType.PRODUCT_LOW_STOCK -> "Stock faible"
        NotificationType.PRODUCT_OUT_OF_STOCK -> "Rupture de stock"
        NotificationType.NEW_PRODUCT_ADDED -> "Nouveau produit ajouté"
        NotificationType.DELIVERY_ASSIGNED -> "Livraison assignée"
        NotificationType.DELIVERY_PICKED_UP -> "Colis récupéré"
        NotificationType.DELIVERY_FAILED -> "Échec de livraison"
        NotificationType.PROMO_ALERT -> "Alerte promotion"
        NotificationType.SYSTEM_ANNOUNCEMENT -> "Annonce système"
    }
}
