package com.example.marchify.navigation

/**
 * Sealed class representing all navigation routes in the app
 * Uses type-safe navigation with parameter passing
 */
sealed class Screen(val route: String) {

    // ==================== AUTH SCREENS ====================
    object Login : Screen("login")
    object Register : Screen("register")


    // ==================== CLIENT SCREENS ====================
    object ClientHome : Screen("client/home")

    object Boutiques : Screen("client/boutiques")

    object BoutiqueDetail : Screen("client/boutique/{boutiqueId}") {
        fun createRoute(boutiqueId: String) = "client/boutique/$boutiqueId"
    }

    object Products : Screen("client/products/{boutiqueId}") {
        fun createRoute(boutiqueId: String) = "client/products/$boutiqueId"
    }

    object ProductDetail : Screen("client/product/{productId}") {
        fun createRoute(productId: String) = "client/product/$productId"
    }

    object Cart : Screen("client/cart")

    object Checkout : Screen("client/checkout")

    object ClientOrders : Screen("client/orders")

    object OrderDetail : Screen("client/order/{orderId}") {
        fun createRoute(orderId: String) = "client/order/$orderId"
    }

    object ClientProfile : Screen("client/profile")

    object Notifications : Screen("client/notifications")


    // ==================== VENDEUR SCREENS ====================
    object VendeurDashboard : Screen("vendeur/dashboard")

    object MyBoutiques : Screen("vendeur/boutiques")

    object AddBoutique : Screen("vendeur/boutique/add")

    object EditBoutique : Screen("vendeur/boutique/edit/{boutiqueId}") {
        fun createRoute(boutiqueId: String) = "vendeur/boutique/edit/$boutiqueId"
    }

    object MyProducts : Screen("vendeur/products")

    object AddProduct : Screen("vendeur/product/add")

    object EditProduct : Screen("vendeur/product/edit/{productId}") {
        fun createRoute(productId: String) = "vendeur/product/edit/$productId"
    }

    object VendeurOrders : Screen("vendeur/orders")

    object VendeurOrderDetail : Screen("vendeur/order/{orderId}") {
        fun createRoute(orderId: String) = "vendeur/order/$orderId"
    }

    object Analytics : Screen("vendeur/analytics")

    object VendeurProfile : Screen("vendeur/profile")


    // ==================== LIVREUR SCREENS ====================
    object Missions : Screen("livreur/missions")

    object MissionDetail : Screen("livreur/mission/{missionId}") {
        fun createRoute(missionId: String) = "livreur/mission/$missionId"
    }

    object Deliveries : Screen("livreur/deliveries")

    object DeliveryDetail : Screen("livreur/delivery/{bonId}") {
        fun createRoute(bonId: String) = "livreur/delivery/$bonId"
    }

    object DeliveryTracking : Screen("livreur/tracking/{bonId}") {
        fun createRoute(bonId: String) = "livreur/tracking/$bonId"
    }

    object LivreurProfile : Screen("livreur/profile")
}
