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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.marchify.ui.components.*
import com.example.marchify.ui.theme.*
import com.example.marchify.utils.DateUtils

/**
 * Order Detail Screen
 * Shows complete order information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            MarchifyTopBar(
                title = "Commande #${orderId.take(8)}",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoadingDetail -> {
                LoadingScreen()
            }
            uiState.selectedOrder == null -> {
                ErrorScreen(
                    message = "Commande introuvable",
                    onRetry = { viewModel.loadOrderDetail(orderId) }
                )
            }
            else -> {
                val order = uiState.selectedOrder!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    // Order Status Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.large)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Statut",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OrderStatusBadge(status = order.status)
                                }

                                Spacer(modifier = Modifier.height(Spacing.medium))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text(
                                        text = DateUtils.formatIsoToReadable(order.dateCommande),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Delivery Address
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.large)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = PrimaryGreen
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Text(
                                        text = "Adresse de livraison",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.medium))

                                Text(
                                    text = "${order.adresseLivraison.rue}\n" +
                                            "${order.adresseLivraison.ville}, ${order.adresseLivraison.codePostal}\n" +
                                            order.adresseLivraison.pays,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Boutique Info (if available)
                    if (order.boutique != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackground
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(Spacing.large),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = PrimaryGreen
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.small))
                                    Column {
                                        Text(
                                            text = "Boutique",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = order.boutique.nom,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Products Section
                    item {
                        Text(
                            text = "Articles (${order.produits.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(order.produits) { item ->
                        item.produit?.let { produit ->
                            OrderProductItem(
                                productName = produit.nom,
                                productImage = produit.image,
                                quantity = item.quantite,
                                price = item.produit.prix,
                                subtotal = item.prixTotal
                            )
                        }
                    }


                    // Total Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryGreen.copy(alpha = 0.05f)
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.large)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Sous-total")
                                    Text(
                                        text = "${order.totalCommande} TND",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.small))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Livraison")
                                    Text(
                                        text = "5.0 TND",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(Spacing.medium))

                                Divider()

                                Spacer(modifier = Modifier.height(Spacing.medium))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${"%.2f".format(order.totalCommande + 5.0)} TND",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                            }
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
private fun OrderProductItem(
    productName: String,
    productImage: String,
    quantity: Int,
    price: Double,
    subtotal: Double
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = productImage,
                contentDescription = productName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(Spacing.extraSmall))

                Text(
                    text = "$quantity × ${"%.2f".format(price)} TND",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = "${"%.2f".format(subtotal)} TND",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
        }
    }
}
