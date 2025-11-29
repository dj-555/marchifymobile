package com.example.marchify.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marchify.ui.auth.LoginScreen
import com.example.marchify.ui.auth.RegisterScreen
import com.example.marchify.utils.Constants
import com.example.marchify.utils.PrefsManager

/**
 * Navigation graph for authentication screens
 * Handles login, register, and role-based navigation after successful auth
 */
fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager
) {

    // ==================== LOGIN SCREEN ====================
    composable(route = Screen.Login.route) {
        LoginScreen(
            onLoginSuccess = { userRole ->
                // Navigate to appropriate home screen based on role
                val destination = when (userRole) {
                    Constants.ROLE_CLIENT -> Screen.ClientHome.route
                    Constants.ROLE_VENDEUR -> Screen.VendeurDashboard.route
                    Constants.ROLE_LIVREUR -> Screen.Missions.route
                    else -> Screen.Login.route
                }

                // Clear back stack and navigate to home
                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onRegisterClick = {
                navController.navigate(Screen.Register.route)
            }
        )
    }


    // ==================== REGISTER SCREEN ====================
    composable(route = Screen.Register.route) {
        RegisterScreen(
            onRegisterSuccess = {
                // After successful registration, go back to login
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            },
            onBackClick = {
                navController.popBackStack()
            },
            onLoginClick = {
                navController.popBackStack()
            }
        )
    }
}
