package com.example.marchify.ui.vendeur

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.PrefsManager

/**
 * Vendeur Dashboard Screen
 * Overview of boutiques and orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBoutiquesClick: () -> Unit,
    onProductsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MarchifyLogoTopBar(
                onCartClick = {}, // Vendeur doesn't need cart
                onNotificationClick = onNotificationsClick,
                notificationCount = 0
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    // Welcome Section
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryGreen.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.large),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Tableau de bord",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Gérez vos boutiques et commandes",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = onProfileClick) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(32.dp),
                                        tint = PrimaryGreen
                                    )
                                }
                            }
                        }
                    }

                    // Quick Stats
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            StatCard(
                                title = "En attente",
                                value = uiState.pendingOrders.toString(),
                                icon = Icons.Default.HourglassEmpty,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "En cours",
                                value = uiState.processingOrders.toString(),
                                icon = Icons.Default.Inventory,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Quick Actions
                    item {
                        Text(
                            text = "Actions rapides",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            QuickActionCard(
                                title = "Boutiques",
                                icon = Icons.Default.Store,
                                onClick = onBoutiquesClick,
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Produits",
                                icon = Icons.Default.Inventory2,
                                onClick = onProductsClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            QuickActionCard(
                                title = "Commandes",
                                icon = Icons.Default.Receipt,
                                onClick = onOrdersClick,
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Statistiques",
                                icon = Icons.Default.Analytics,
                                onClick = onAnalyticsClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Recent Orders
                    if (uiState.recentOrders.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Commandes récentes",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = onOrdersClick) {
                                    Text("Voir tout")
                                }
                            }
                        }

                        items(uiState.recentOrders) { order ->
                            OrderCardCompact(
                                order = order,
                                onClick = { /* TODO: Navigate to order detail */ },
                                showCustomerInfo = true
                            )
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
