package com.example.marchify.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.ui.client.OrderDetailScreen
import com.example.marchify.ui.client.ProfileScreen
import com.example.marchify.ui.vendeur.*
import com.example.marchify.utils.PrefsManager

/**
 * Navigation graph for VENDEUR role screens
 * Shop management: Dashboard → Boutiques → Products → Orders → Analytics
 */
fun NavGraphBuilder.vendeurNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager,
    boutiqueRepository: BoutiqueRepository,
    productRepository: ProductRepository
) {

    // ==================== DASHBOARD ====================
    composable(route = Screen.VendeurDashboard.route) {
        DashboardScreen(
            onBoutiquesClick = { navController.navigate(Screen.MyBoutiques.route) },
            onProductsClick = { navController.navigate(Screen.MyProducts.route) },
            onOrdersClick = { navController.navigate(Screen.VendeurOrders.route) },
            onAnalyticsClick = { navController.navigate(Screen.Analytics.route) },
            onProfileClick = { navController.navigate(Screen.VendeurProfile.route) },
            onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
        )
    }

    // ==================== MY BOUTIQUES ====================
    composable(route = Screen.MyBoutiques.route) {
        MyBoutiquesScreen(
            onAddBoutiqueClick = { navController.navigate(Screen.AddBoutique.route) },
            onBoutiqueClick = { boutiqueId ->
                navController.navigate(Screen.EditBoutique.createRoute(boutiqueId))
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    // ==================== ADD BOUTIQUE ====================
    composable(route = Screen.AddBoutique.route) {
        AddBoutiqueScreen(
            onSuccess = { navController.popBackStack() },
            onBackClick = { navController.popBackStack() },
            boutiqueRepository = boutiqueRepository,
            prefsManager = prefsManager
        )
    }

    // ==================== EDIT BOUTIQUE ====================
    composable(
        route = Screen.EditBoutique.route,
        arguments = listOf(navArgument("boutiqueId") { type = NavType.StringType })
    ) { backStackEntry ->
        val boutiqueId = backStackEntry.arguments?.getString("boutiqueId") ?: ""
        EditBoutiqueScreen(
            boutiqueId = boutiqueId,
            onSuccess = { navController.popBackStack() },
            onBackClick = { navController.popBackStack() },
            boutiqueRepository = boutiqueRepository
        )
    }

    // ==================== MY PRODUCTS ====================
    composable(route = Screen.MyProducts.route) {
        MyProductsScreen(
            onProductClick = { productId ->
                navController.navigate(Screen.EditProduct.createRoute(productId))
            },
            onAddProductClick = { navController.navigate(Screen.AddProduct.route) },
            onEditProductClick = { productId ->
                navController.navigate(Screen.EditProduct.createRoute(productId))
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    // ==================== ADD PRODUCT ====================
    composable(route = Screen.AddProduct.route) {
        AddProductScreen(
            onSuccess = { navController.popBackStack() },
            onBackClick = { navController.popBackStack() },
            boutiqueRepository = boutiqueRepository,
            productRepository = productRepository,
            prefsManager = prefsManager
        )
    }

    // ==================== EDIT PRODUCT ====================
    composable(
        route = Screen.EditProduct.route,
        arguments = listOf(navArgument("productId") { type = NavType.StringType })
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: ""
        EditProductScreen(
            productId = productId,
            onSuccess = { navController.popBackStack() },
            onBackClick = { navController.popBackStack() },
            productRepository = productRepository
        )
    }

    // ==================== VENDEUR ORDERS ====================
    composable(route = Screen.VendeurOrders.route) {
        OrdersScreen(
            onOrderClick = { orderId ->
                navController.navigate(Screen.VendeurOrderDetail.createRoute(orderId))
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    // ==================== VENDEUR ORDER DETAIL ====================
    composable(
        route = Screen.VendeurOrderDetail.route,
        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        OrderDetailScreen(
            orderId = orderId,
            onBackClick = { navController.popBackStack() }
        )
    }

    // ==================== ANALYTICS ====================
    composable(route = Screen.Analytics.route) {
        AnalyticsScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    // ==================== PROFILE ====================
    composable(route = Screen.VendeurProfile.route) {
        ProfileScreen(
            onLogout = {
                prefsManager.clearAuth()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }
}
