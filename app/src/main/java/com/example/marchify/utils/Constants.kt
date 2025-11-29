package com.example.marchify.utils

/**
 * Application-wide constants
 */
object Constants {

    // ==================== API ====================
// ✅ YOUR CORRECT IP!
    const val BASE_URL = "http://192.168.1.194:3000/api/"
    // const val BASE_URL = "http://192.168.1.x:3000/api/" // Real device - use your IP

    const val TIMEOUT_SECONDS = 30L
    const val CONNECT_TIMEOUT = TIMEOUT_SECONDS
    const val READ_TIMEOUT = TIMEOUT_SECONDS
    const val WRITE_TIMEOUT = TIMEOUT_SECONDS

    // ==================== USER ROLES ====================
    const val ROLE_CLIENT = "CLIENT"
    const val ROLE_VENDEUR = "VENDEUR"
    const val ROLE_LIVREUR = "LIVREUR"

    // ==================== ORDER STATUS ====================
    const val ORDER_STATUS_EN_ATTENTE = "EN_ATTENTE"
    const val ORDER_STATUS_CONFIRMEE = "CONFIRMEE"
    const val ORDER_STATUS_EN_PREPARATION = "EN_PREPARATION"
    const val ORDER_STATUS_PRETE = "PRETE"
    const val ORDER_STATUS_EN_LIVRAISON = "EN_LIVRAISON"
    const val ORDER_STATUS_LIVREE = "LIVREE"
    const val ORDER_STATUS_ANNULEE = "ANNULEE"

    // ==================== MISSION STATUS ====================
    const val MISSION_STATUS_EN_ATTENTE = "EN_ATTENTE"
    const val MISSION_STATUS_ACCEPTEE = "ACCEPTEE"
    const val MISSION_STATUS_EN_COURS = "EN_COURS"
    const val MISSION_STATUS_RECUPEREE = "RECUPEREE"
    const val MISSION_STATUS_EN_LIVRAISON = "EN_LIVRAISON"
    const val MISSION_STATUS_LIVREE = "LIVREE"
    const val MISSION_STATUS_ANNULEE = "ANNULEE"

    // ==================== PAYMENT METHODS ====================
    const val PAYMENT_ESPECES = "ESPECES"
    const val PAYMENT_CARTE = "CARTE"
    const val PAYMENT_EN_LIGNE = "EN_LIGNE"

    // ==================== PRODUCT CATEGORIES ====================
    val PRODUCT_CATEGORIES = listOf(
        "Fruits",
        "Légumes",
        "Viandes",
        "Poissons",
        "Épices",
        "Produits laitiers",
        "Céréales",
        "Autres"
    )

    // ==================== VALIDATION ====================
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_PHONE_LENGTH = 8

    // ==================== PAGINATION ====================
    const val PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5

    // ==================== IMAGE ====================
    const val MAX_IMAGE_SIZE_MB = 5
    const val IMAGE_QUALITY = 80

    // ==================== MAPS ====================
    const val DEFAULT_LATITUDE = 36.8065  // Tunis
    const val DEFAULT_LONGITUDE = 10.1815 // Tunis
    const val DEFAULT_ZOOM = 12f

    // ==================== NOTIFICATION CHANNELS ====================
    const val CHANNEL_ORDER = "order_channel"
    const val CHANNEL_MISSION = "mission_channel"
    const val CHANNEL_GENERAL = "general_channel"
}
