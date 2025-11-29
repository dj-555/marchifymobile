package com.example.marchify.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.marchify.ui.theme.PrimaryGreen

/**
 * Bottom navigation bar for CLIENT role
 */
@Composable
fun ClientBottomBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        BottomNavItem(
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            label = "Accueil",
            selected = selectedRoute.contains("home"),
            onClick = { onNavigate("client/home") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Store,
            selectedIcon = Icons.Filled.Store,
            label = "Boutiques",
            selected = selectedRoute.contains("boutiques"),
            onClick = { onNavigate("client/boutiques") }
        )

        BottomNavItem(
            icon = Icons.Outlined.ShoppingCart,
            selectedIcon = Icons.Filled.ShoppingCart,
            label = "Panier",
            selected = selectedRoute.contains("cart"),
            onClick = { onNavigate("client/cart") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Receipt,
            selectedIcon = Icons.Filled.Receipt,
            label = "Commandes",
            selected = selectedRoute.contains("orders"),
            onClick = { onNavigate("client/orders") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            label = "Profil",
            selected = selectedRoute.contains("profile"),
            onClick = { onNavigate("client/profile") }
        )
    }
}

/**
 * Bottom navigation bar for VENDEUR role
 */
@Composable
fun VendeurBottomBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        BottomNavItem(
            icon = Icons.Outlined.Dashboard,
            selectedIcon = Icons.Filled.Dashboard,
            label = "Tableau",
            selected = selectedRoute.contains("dashboard"),
            onClick = { onNavigate("vendeur/dashboard") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Store,
            selectedIcon = Icons.Filled.Store,
            label = "Boutiques",
            selected = selectedRoute.contains("boutiques"),
            onClick = { onNavigate("vendeur/boutiques") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Inventory,
            selectedIcon = Icons.Filled.Inventory,
            label = "Produits",
            selected = selectedRoute.contains("products"),
            onClick = { onNavigate("vendeur/products") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Receipt,
            selectedIcon = Icons.Filled.Receipt,
            label = "Commandes",
            selected = selectedRoute.contains("orders"),
            onClick = { onNavigate("vendeur/orders") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            label = "Profil",
            selected = selectedRoute.contains("profile"),
            onClick = { onNavigate("vendeur/profile") }
        )
    }
}

/**
 * Bottom navigation bar for LIVREUR role
 */
@Composable
fun LivreurBottomBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        BottomNavItem(
            icon = Icons.Outlined.Assignment,
            selectedIcon = Icons.Filled.Assignment,
            label = "Missions",
            selected = selectedRoute.contains("missions"),
            onClick = { onNavigate("livreur/missions") }
        )

        BottomNavItem(
            icon = Icons.Outlined.LocalShipping,
            selectedIcon = Icons.Filled.LocalShipping,
            label = "Livraisons",
            selected = selectedRoute.contains("deliveries"),
            onClick = { onNavigate("livreur/deliveries") }
        )

        BottomNavItem(
            icon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            label = "Profil",
            selected = selectedRoute.contains("profile"),
            onClick = { onNavigate("livreur/profile") }
        )
    }
}

/**
 * Individual bottom navigation item
 */
@Composable
private fun RowScope.BottomNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected) selectedIcon else icon,
                contentDescription = label
            )
        },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = PrimaryGreen,
            selectedTextColor = PrimaryGreen,
            indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
        )
    )
}
