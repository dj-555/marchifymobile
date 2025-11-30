package com.example.marchify.ui.client

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
 * Boutique Detail Screen
 * Shows boutique info and its products
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoutiqueDetailScreen(
    boutiqueId: String,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(
            PrefsManager(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()


    // Only trigger once per boutiqueId
    LaunchedEffect( boutiqueId) {
        viewModel.loadProductsByBoutique(boutiqueId)
        viewModel.loadBoutiqueDetails(boutiqueId)
    }
    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = uiState.boutique?.nom ?: "Boutique",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadProductsByBoutique(boutiqueId) }
                )
            }
            uiState.boutique != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Boutique Header
                    item {
                        BoutiqueHeader(
                            boutique = uiState.boutique!!
                        )
                    }

                    // Products Section
                    item {
                        Text(
                            text = "Produits (${uiState.products.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(Spacing.medium)
                        )
                    }

                    if (uiState.products.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.Inventory,
                                title = "Aucun produit",
                                message = "Cette boutique n'a pas encore de produits."
                            )
                        }
                    } else {
                        items(uiState.products) { product ->
                            ProductCardCompact(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) },
                                modifier = Modifier.padding(
                                    horizontal = Spacing.medium,
                                    vertical = Spacing.small
                                )
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
private fun BoutiqueHeader(
    boutique: com.example.marchify.api.models.Boutique
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large)
        ) {
            // Header with icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = boutique.nom,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(Spacing.small))

                    // Category badge
                    Surface(
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = boutique.categorie,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Store icon
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = PrimaryGreen.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            Divider(color = BorderLight)

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Error
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = boutique.adresse,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            // Phone
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = boutique.telephone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Ratings (if available)
            if (boutique.averageRating != null && boutique.totalReviews != null) {
                Spacer(modifier = Modifier.height(Spacing.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = AccentOrange
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = "${"%.1f".format(boutique.averageRating)} (${boutique.totalReviews} avis)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Product count
            if (boutique.produits != null) {
                Spacer(modifier = Modifier.height(Spacing.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = "${boutique.produits.size} produits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
