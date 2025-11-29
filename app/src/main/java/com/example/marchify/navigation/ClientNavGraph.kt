package com.example.marchify.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.marchify.ui.client.*
import com.example.marchify.ui.notifications.NotificationsScreen
import com.example.marchify.utils.PrefsManager

/**
 * Navigation graph for CLIENT role screens
 * Shopping flow: Browse → Boutiques → Products → Cart → Checkout → Orders
 */
fun NavGraphBuilder.clientNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager
) {

    // ==================== HOME SCREEN ====================
    composable(route = Screen.ClientHome.route) {
        HomeScreen(
            onBoutiquesClick = {
                navController.navigate(Screen.Boutiques.route)
            },
            onCartClick = {
                navController.navigate(Screen.Cart.route)
            },
            onOrdersClick = {
                navController.navigate(Screen.ClientOrders.route)
            },
            onProfileClick = {
                navController.navigate(Screen.ClientProfile.route)
            },
            onNotificationsClick = {
                navController.navigate(Screen.Notifications.route)
            },
            onProductClick = { productId ->
                navController.navigate(Screen.ProductDetail.createRoute(productId))
            },
            onBoutiqueClick = { boutiqueId ->
                navController.navigate(Screen.BoutiqueDetail.createRoute(boutiqueId))
            }
        )
    }


    // ==================== BOUTIQUES LIST ====================
    composable(route = Screen.Boutiques.route) {
        BoutiquesScreen(
            onBoutiqueClick = { boutiqueId ->
                navController.navigate(Screen.BoutiqueDetail.createRoute(boutiqueId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== BOUTIQUE DETAIL ====================
    composable(
        route = Screen.BoutiqueDetail.route,
        arguments = listOf(
            navArgument("boutiqueId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val boutiqueId = backStackEntry.arguments?.getString("boutiqueId") ?: ""

        BoutiqueDetailScreen(
            boutiqueId = boutiqueId,
            onProductClick = { productId ->
                navController.navigate(Screen.ProductDetail.createRoute(productId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== PRODUCTS SCREEN ====================
    composable(
        route = Screen.Products.route,
        arguments = listOf(
            navArgument("boutiqueId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val boutiqueId = backStackEntry.arguments?.getString("boutiqueId") ?: ""

        ProductsScreen(
            boutiqueId = boutiqueId,
            onProductClick = { productId ->
                navController.navigate(Screen.ProductDetail.createRoute(productId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== PRODUCT DETAIL ====================
    composable(
        route = Screen.ProductDetail.route,
        arguments = listOf(
            navArgument("productId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: ""

        ProductDetailScreen(
            productId = productId,
            onAddToCart = {
                // Show success message, stay on page
            },
            onCartClick = {
                navController.navigate(Screen.Cart.route)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== CART SCREEN ====================
    composable(route = Screen.Cart.route) {
        CartScreen(
            onCheckoutClick = {
                navController.navigate(Screen.Checkout.route)
            },
            onProductClick = { productId ->
                navController.navigate(Screen.ProductDetail.createRoute(productId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== CHECKOUT SCREEN ====================
    composable(route = Screen.Checkout.route) {
        CheckoutScreen(
            onOrderSuccess = {
                // Navigate to orders and clear back stack
                navController.navigate(Screen.ClientOrders.route) {
                    popUpTo(Screen.ClientHome.route)
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== ORDERS LIST ====================
    composable(route = Screen.ClientOrders.route) {
        OrdersScreen(
            onOrderClick = { orderId ->
                navController.navigate(Screen.OrderDetail.createRoute(orderId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== ORDER DETAIL ====================
    composable(
        route = Screen.OrderDetail.route,
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""

        OrderDetailScreen(
            orderId = orderId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== PROFILE SCREEN ====================
    composable(route = Screen.ClientProfile.route) {
        ProfileScreen(
            onLogout = {
                prefsManager.clearAuth()  // ✅ CORRECT
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }


    },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== NOTIFICATIONS ====================
    composable(route = Screen.Notifications.route) {
        NotificationsScreen(
            onNotificationClick = { notification ->
                // Handle notification navigation based on type
                notification.commandeId?.let { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
