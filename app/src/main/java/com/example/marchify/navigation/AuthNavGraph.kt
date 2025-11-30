package com.example.marchify.navigation

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.marchify.ui.auth.LoginScreen
import com.example.marchify.ui.auth.RegisterScreen
import com.example.marchify.utils.Constants
import com.example.marchify.utils.PrefsManager

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager
) {

    composable(route = Screen.Login.route) {
        LoginScreen(
            onLoginSuccess = { userRole ->
                // DEBUG LOGS
                Log.d("AUTH_NAV", "=== Login Success ===")
                Log.d("AUTH_NAV", "User Role: '$userRole'")
                Log.d("AUTH_NAV", "Role Type: ${userRole.javaClass.name}")
                Log.d("AUTH_NAV", "ROLE_CLIENT: '${Constants.ROLE_CLIENT}'")
                Log.d("AUTH_NAV", "ROLE_VENDEUR: '${Constants.ROLE_VENDEUR}'")
                Log.d("AUTH_NAV", "ROLE_LIVREUR: '${Constants.ROLE_LIVREUR}'")
                Log.d("AUTH_NAV", "userRole == ROLE_VENDEUR: ${userRole == Constants.ROLE_VENDEUR}")

                val destination = when (userRole) {
                    Constants.ROLE_CLIENT -> {
                        Log.d("AUTH_NAV", "Going to ClientHome")
                        Screen.ClientHome.route
                    }
                    Constants.ROLE_VENDEUR -> {
                        Log.d("AUTH_NAV", "Going to VendeurDashboard")
                        Screen.VendeurDashboard.route
                    }
                    Constants.ROLE_LIVREUR -> {
                        Log.d("AUTH_NAV", "Going to Missions")
                        Screen.Missions.route
                    }
                    else -> {
                        Log.e("AUTH_NAV", "Unknown role! Going to Login")
                        Screen.Login.route
                    }
                }

                Log.d("AUTH_NAV", "Final destination: $destination")
                Log.d("AUTH_NAV", "===================")

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

    composable(route = Screen.Register.route) {
        RegisterScreen(
            onRegisterSuccess = {
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
