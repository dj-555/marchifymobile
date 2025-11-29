package com.example.marchify.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.Spacing

/**
 * Home Screen - Client dashboard
 * Shows featured products, popular boutiques, categories
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBoutiquesClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onBoutiqueClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            MarchifyLogoTopBar(
                onCartClick = onCartClick,
                cartItemCount = uiState.cartItemCount,
                onNotificationClick = onNotificationsClick,
                notificationCount = 0
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadHomeData() }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Welcome Banner
                    item {
                        WelcomeBanner(
                            onExploreClick = onBoutiquesClick
                        )
                    }

                    // Quick Actions
                    item {
                        QuickActions(
                            onBoutiquesClick = onBoutiquesClick,
                            onOrdersClick = onOrdersClick,
                            onProfileClick = onProfileClick
                        )
                    }

                    // Featured Products Section
                    item {
                        SectionHeader(
                            title = "Produits vedettes",
                            onSeeAllClick = onBoutiquesClick
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = Spacing.medium),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            items(uiState.featuredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    onAddToCart = { viewModel.addToCart(product.id) }
                                )
                            }
                        }
                    }

                    // Popular Boutiques Section
                    item {
                        SectionHeader(
                            title = "Boutiques populaires",
                            onSeeAllClick = onBoutiquesClick
                        )
                    }

                    items(uiState.popularBoutiques) { boutique ->
                        BoutiqueCard(
                            boutique = boutique,
                            onClick = { onBoutiqueClick(boutique.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                        )
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
private fun WelcomeBanner(
    onExploreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large)
        ) {
            Text(
                text = "Bienvenue sur MarchiFy ! 🛒",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = "Découvrez nos boutiques partenaires et explorez leurs univers uniques.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Button(onClick = onExploreClick) {
                Text("Explorer")
                Spacer(modifier = Modifier.width(Spacing.small))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun QuickActions(
    onBoutiquesClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        QuickActionCard(
            icon = Icons.Default.Store,
            label = "Boutiques",
            onClick = onBoutiquesClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Default.Receipt,
            label = "Commandes",
            onClick = onOrdersClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Default.Person,
            label = "Profil",
            onClick = onProfileClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = onSeeAllClick) {
            Text("Voir tout")
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
