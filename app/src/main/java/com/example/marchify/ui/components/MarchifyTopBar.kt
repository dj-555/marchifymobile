package com.example.marchify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.marchify.ui.theme.PrimaryGreen
import com.example.marchify.ui.theme.TextOnPrimary

/**
 * Standard top app bar for MarchiFy app
 * Supports back button, title, and action icons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarchifyTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    showCart: Boolean = false,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {},
    showNotifications: Boolean = false,
    notificationCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            }
        },
        actions = {
            // Custom actions
            actions()

            // Notifications
            if (showNotifications) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge { Text(notificationCount.toString()) }
                        }
                    }
                ) {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            }

            // Shopping Cart
            if (showCart) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge { Text(cartItemCount.toString()) }
                        }
                    }
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Panier"
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryGreen,
            titleContentColor = TextOnPrimary,
            navigationIconContentColor = TextOnPrimary,
            actionIconContentColor = TextOnPrimary
        )
    )
}

/**
 * Simple top bar with logo (for home screens)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarchifyLogoTopBar(
    onCartClick: () -> Unit = {},
    cartItemCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    notificationCount: Int = 0
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "MarchiFy",
                    tint = TextOnPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MarchiFy",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            // Notifications
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge { Text(notificationCount.toString()) }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }

            // Cart
            BadgedBox(
                badge = {
                    if (cartItemCount > 0) {
                        Badge { Text(cartItemCount.toString()) }
                    }
                }
            ) {
                IconButton(onClick = onCartClick) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Panier"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryGreen,
            titleContentColor = TextOnPrimary,
            actionIconContentColor = TextOnPrimary
        )
    )
}
