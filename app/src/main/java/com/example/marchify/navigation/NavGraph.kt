package com.example.marchify.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.Constants
import com.example.marchify.utils.PrefsManager

/**
 * Main navigation graph that delegates to role-specific sub-graphs
 */
@Composable
fun MarchifyNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager
) {
    // ✅ Create repositories with PrefsManager (required by constructors)
    val boutiqueRepository = remember(prefsManager) {
        BoutiqueRepository(prefsManager)
    }
    val productRepository = remember(prefsManager) {
        ProductRepository(prefsManager)
    }

    // Determine start destination
    val isLoggedIn = prefsManager.isLoggedIn()
    val userRole = prefsManager.getUserRole()

    val startDestination by remember(isLoggedIn, userRole) {
        derivedStateOf {
            when {
                !isLoggedIn -> Screen.Login.route
                userRole == Constants.ROLE_CLIENT -> Screen.ClientHome.route
                userRole == Constants.ROLE_VENDEUR -> Screen.VendeurDashboard.route
                userRole == Constants.ROLE_LIVREUR -> Screen.Missions.route
                else -> Screen.Login.route
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController, prefsManager)
        clientNavGraph(navController, prefsManager)
        vendeurNavGraph(
            navController = navController,
            prefsManager = prefsManager,
            boutiqueRepository = boutiqueRepository,
            productRepository = productRepository
        )
        livreurNavGraph(navController, prefsManager)
    }
}
