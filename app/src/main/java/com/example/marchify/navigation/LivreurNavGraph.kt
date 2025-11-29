package com.example.marchify.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.marchify.ui.client.ProfileScreen
import com.example.marchify.ui.livreur.*
import com.example.marchify.utils.PrefsManager

/**
 * Navigation graph for LIVREUR role screens
 * Delivery flow: Missions → Accept → Pickup → Track → Deliver
 */
fun NavGraphBuilder.livreurNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager
) {

    // ==================== MISSIONS LIST ====================
    composable(route = Screen.Missions.route) {
        MissionsScreen(
            onMissionClick = { missionId ->
                navController.navigate(Screen.MissionDetail.createRoute(missionId))
            },
            onDeliveriesClick = {
                navController.navigate(Screen.Deliveries.route)
            },
            onProfileClick = {
                navController.navigate(Screen.LivreurProfile.route)
            },
            onNotificationsClick = {
                navController.navigate(Screen.Notifications.route)
            }
        )
    }


    // ==================== MISSION DETAIL ====================
    composable(
        route = Screen.MissionDetail.route,
        arguments = listOf(
            navArgument("missionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val missionId = backStackEntry.arguments?.getString("missionId") ?: ""

        MissionDetailScreen(
            missionId = missionId,
            onAcceptMission = {
                // After accepting, navigate to deliveries
                navController.navigate(Screen.Deliveries.route) {
                    popUpTo(Screen.Missions.route)
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== ACTIVE DELIVERIES ====================
    composable(route = Screen.Deliveries.route) {
        DeliveriesScreen(
            onDeliveryClick = { bonId ->
                navController.navigate(Screen.DeliveryTracking.createRoute(bonId))
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== DELIVERY TRACKING (MAP) ====================
    composable(
        route = Screen.DeliveryTracking.route,
        arguments = listOf(
            navArgument("bonId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val bonId = backStackEntry.arguments?.getString("bonId") ?: ""

        DeliveryTrackingScreen(
            bonId = bonId,
            onOpenMap = {
                // Handle opening external map app if needed
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }


    // ==================== PROFILE ====================
    composable(route = Screen.LivreurProfile.route) {
        ProfileScreen(
            onLogout = {
                prefsManager.clearAuth()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
